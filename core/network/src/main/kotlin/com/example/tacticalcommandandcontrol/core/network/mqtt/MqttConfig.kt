package com.example.tacticalcommandandcontrol.core.network.mqtt

data class MqttConfig(
    val brokerHost: String = "10.0.2.2",
    val brokerPort: Int = 8883,
    val clientId: String = "c2-tablet-${System.currentTimeMillis()}",
    val username: String? = null,
    val password: String? = null,
    val useTls: Boolean = true,
    val keepAliveSeconds: Int = 30,
    val connectionTimeoutSeconds: Long = 10,
    val reconnectDelayMs: Long = 5000,
    val maxReconnectAttempts: Int = 10,
)
