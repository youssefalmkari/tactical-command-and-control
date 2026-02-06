package com.example.tacticalcommandandcontrol.core.domain.model

/**
 * Role-Based Access Control (RBAC) for C2 operators.
 *
 * Each role defines what actions an operator can perform.
 * Roles are hierarchical: higher roles include all permissions of lower roles.
 */
enum class OperatorRole {
    /** View-only access to telemetry and mission status. No command authority. */
    OBSERVER,

    /** Can send flight commands to assigned drones. Cannot create/modify missions. */
    PILOT,

    /** Full mission CRUD + pilot authority. Can assign drones to missions. */
    MISSION_COMMANDER,

    /** Unrestricted access including system configuration and key management. */
    ADMIN,
}

/**
 * Permissions that can be checked against an operator's role.
 */
enum class OperatorPermission {
    VIEW_TELEMETRY,
    VIEW_MISSIONS,
    SEND_FLIGHT_COMMANDS,
    EMERGENCY_STOP,
    CREATE_MISSION,
    EDIT_MISSION,
    DELETE_MISSION,
    ASSIGN_DRONES,
    MANAGE_OPERATORS,
    CONFIGURE_SYSTEM,
}

/**
 * Returns the set of permissions granted to this role.
 */
fun OperatorRole.permissions(): Set<OperatorPermission> = when (this) {
    OperatorRole.OBSERVER -> setOf(
        OperatorPermission.VIEW_TELEMETRY,
        OperatorPermission.VIEW_MISSIONS,
    )
    OperatorRole.PILOT -> OperatorRole.OBSERVER.permissions() + setOf(
        OperatorPermission.SEND_FLIGHT_COMMANDS,
        OperatorPermission.EMERGENCY_STOP,
    )
    OperatorRole.MISSION_COMMANDER -> OperatorRole.PILOT.permissions() + setOf(
        OperatorPermission.CREATE_MISSION,
        OperatorPermission.EDIT_MISSION,
        OperatorPermission.DELETE_MISSION,
        OperatorPermission.ASSIGN_DRONES,
    )
    OperatorRole.ADMIN -> OperatorPermission.entries.toSet()
}

/**
 * Check if this role has a specific permission.
 */
fun OperatorRole.hasPermission(permission: OperatorPermission): Boolean =
    permission in permissions()
