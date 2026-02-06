package com.example.tacticalcommandandcontrol.core.network.mqtt

import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttClientManager @Inject constructor(
    private val config: MqttConfig,
    private val scope: CoroutineScope,
) {
    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    val connectionState: StateFlow<MqttConnectionState> = _connectionState.asStateFlow()

    private var client: Mqtt5AsyncClient? = null
    private var reconnectAttempt = 0

    fun connect() {
        if (_connectionState.value is MqttConnectionState.Connected ||
            _connectionState.value is MqttConnectionState.Connecting
        ) return

        _connectionState.value = MqttConnectionState.Connecting

        val builder = Mqtt5Client.builder()
            .identifier(config.clientId)
            .serverHost(config.brokerHost)
            .serverPort(config.brokerPort)
            .automaticReconnectWithDefaultConfig()

        if (config.useTls) {
            val sslBuilder = builder.sslConfig()
                .trustManagerFactory(config.tlsConfig.buildTrustManagerFactory())
                .protocols(config.tlsConfig.protocols)

            config.tlsConfig.buildKeyManagerFactory()?.let { kmf ->
                sslBuilder.keyManagerFactory(kmf)
            }

            sslBuilder.applySslConfig()
        }

        val mqttClient = builder
            .addDisconnectedListener { context ->
                Timber.w("MQTT disconnected: ${context.cause.message}")
                if (reconnectAttempt < config.maxReconnectAttempts) {
                    reconnectAttempt++
                    _connectionState.value = MqttConnectionState.Reconnecting(reconnectAttempt)
                } else {
                    _connectionState.value = MqttConnectionState.Error(
                        context.cause
                    )
                }
            }
            .addConnectedListener {
                Timber.i("MQTT connected")
                reconnectAttempt = 0
                _connectionState.value = MqttConnectionState.Connected
            }
            .buildAsync()

        client = mqttClient

        val connectBuilder = mqttClient.connectWith()
            .keepAlive(config.keepAliveSeconds)
            .cleanStart(true)

        val connectFuture = if (config.username != null && config.password != null) {
            connectBuilder
                .simpleAuth()
                .username(config.username)
                .password(config.password.toByteArray())
                .applySimpleAuth()
                .send()
        } else {
            connectBuilder.send()
        }

        connectFuture.whenComplete { _, throwable ->
            if (throwable != null) {
                Timber.e(throwable, "MQTT connection failed")
                _connectionState.value = MqttConnectionState.Error(throwable)
                scheduleReconnect()
            }
        }
    }

    fun disconnect() {
        client?.disconnect()?.whenComplete { _, throwable ->
            if (throwable != null) {
                Timber.e(throwable, "MQTT disconnect error")
            }
        }
        _connectionState.value = MqttConnectionState.Disconnected
        client = null
    }

    fun subscribe(topicFilter: String, qos: MqttQos = MqttQos.AT_LEAST_ONCE): Flow<Mqtt5Publish> =
        callbackFlow {
            val mqttClient = client ?: run {
                close()
                return@callbackFlow
            }

            mqttClient.subscribeWith()
                .topicFilter(topicFilter)
                .qos(qos)
                .callback { publish ->
                    trySend(publish)
                }
                .send()
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        Timber.e(throwable, "MQTT subscribe failed: $topicFilter")
                        close(throwable)
                    } else {
                        Timber.d("MQTT subscribed: $topicFilter")
                    }
                }

            awaitClose {
                mqttClient.unsubscribeWith()
                    .topicFilter(topicFilter)
                    .send()
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Timber.e(throwable, "MQTT unsubscribe failed: $topicFilter")
                        }
                    }
            }
        }

    fun publish(
        topic: String,
        payload: ByteArray,
        qos: MqttQos = MqttQos.AT_LEAST_ONCE,
        retain: Boolean = false,
    ): CompletableFuture<Void> {
        val mqttClient = client
            ?: return CompletableFuture.failedFuture(IllegalStateException("MQTT client not connected"))

        return mqttClient.publishWith()
            .topic(topic)
            .payload(payload)
            .qos(qos)
            .retain(retain)
            .send()
            .thenApply { null }
    }

    private fun scheduleReconnect() {
        scope.launch {
            delay(config.reconnectDelayMs)
            if (_connectionState.value is MqttConnectionState.Error ||
                _connectionState.value is MqttConnectionState.Reconnecting
            ) {
                connect()
            }
        }
    }
}
