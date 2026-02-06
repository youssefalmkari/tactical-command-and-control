package com.example.tacticalcommandandcontrol.core.domain.model

sealed class CommandResult {
    data object Acknowledged : CommandResult()
    data class Rejected(val reason: String) : CommandResult()
    data object Timeout : CommandResult()
    data object Queued : CommandResult()
}
