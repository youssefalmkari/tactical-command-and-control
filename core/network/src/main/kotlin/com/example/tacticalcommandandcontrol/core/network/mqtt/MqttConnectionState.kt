package com.example.tacticalcommandandcontrol.core.network.mqtt

sealed interface MqttConnectionState {
    data object Disconnected : MqttConnectionState
    data object Connecting : MqttConnectionState
    data object Connected : MqttConnectionState
    data class Reconnecting(val attempt: Int) : MqttConnectionState
    data class Error(val cause: Throwable) : MqttConnectionState
}
