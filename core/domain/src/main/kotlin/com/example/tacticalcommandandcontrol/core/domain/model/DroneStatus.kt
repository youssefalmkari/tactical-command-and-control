package com.example.tacticalcommandandcontrol.core.domain.model

enum class DroneStatus {
    UNKNOWN,
    IDLE,
    ARMED,
    FLYING,
    RETURNING,
    LANDING,
    LANDED,
    LOST_LINK,
}
