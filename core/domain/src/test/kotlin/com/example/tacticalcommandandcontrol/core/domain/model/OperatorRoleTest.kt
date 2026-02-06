package com.example.tacticalcommandandcontrol.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OperatorRoleTest {

    @Test
    fun `observer has only view permissions`() {
        val perms = OperatorRole.OBSERVER.permissions()
        assertEquals(2, perms.size)
        assertTrue(perms.contains(OperatorPermission.VIEW_TELEMETRY))
        assertTrue(perms.contains(OperatorPermission.VIEW_MISSIONS))
    }

    @Test
    fun `observer cannot send commands`() {
        assertFalse(OperatorRole.OBSERVER.hasPermission(OperatorPermission.SEND_FLIGHT_COMMANDS))
        assertFalse(OperatorRole.OBSERVER.hasPermission(OperatorPermission.EMERGENCY_STOP))
        assertFalse(OperatorRole.OBSERVER.hasPermission(OperatorPermission.CREATE_MISSION))
    }

    @Test
    fun `pilot inherits observer permissions`() {
        val pilotPerms = OperatorRole.PILOT.permissions()
        val observerPerms = OperatorRole.OBSERVER.permissions()
        assertTrue(pilotPerms.containsAll(observerPerms))
    }

    @Test
    fun `pilot can send flight commands`() {
        assertTrue(OperatorRole.PILOT.hasPermission(OperatorPermission.SEND_FLIGHT_COMMANDS))
        assertTrue(OperatorRole.PILOT.hasPermission(OperatorPermission.EMERGENCY_STOP))
    }

    @Test
    fun `pilot cannot manage missions`() {
        assertFalse(OperatorRole.PILOT.hasPermission(OperatorPermission.CREATE_MISSION))
        assertFalse(OperatorRole.PILOT.hasPermission(OperatorPermission.DELETE_MISSION))
    }

    @Test
    fun `mission commander inherits pilot permissions`() {
        val commanderPerms = OperatorRole.MISSION_COMMANDER.permissions()
        val pilotPerms = OperatorRole.PILOT.permissions()
        assertTrue(commanderPerms.containsAll(pilotPerms))
    }

    @Test
    fun `mission commander can manage missions`() {
        assertTrue(OperatorRole.MISSION_COMMANDER.hasPermission(OperatorPermission.CREATE_MISSION))
        assertTrue(OperatorRole.MISSION_COMMANDER.hasPermission(OperatorPermission.EDIT_MISSION))
        assertTrue(OperatorRole.MISSION_COMMANDER.hasPermission(OperatorPermission.DELETE_MISSION))
        assertTrue(OperatorRole.MISSION_COMMANDER.hasPermission(OperatorPermission.ASSIGN_DRONES))
    }

    @Test
    fun `mission commander cannot manage operators`() {
        assertFalse(OperatorRole.MISSION_COMMANDER.hasPermission(OperatorPermission.MANAGE_OPERATORS))
        assertFalse(OperatorRole.MISSION_COMMANDER.hasPermission(OperatorPermission.CONFIGURE_SYSTEM))
    }

    @Test
    fun `admin has all permissions`() {
        val adminPerms = OperatorRole.ADMIN.permissions()
        assertEquals(OperatorPermission.entries.toSet(), adminPerms)
    }

    @Test
    fun `roles are hierarchically ordered`() {
        val observerCount = OperatorRole.OBSERVER.permissions().size
        val pilotCount = OperatorRole.PILOT.permissions().size
        val commanderCount = OperatorRole.MISSION_COMMANDER.permissions().size
        val adminCount = OperatorRole.ADMIN.permissions().size

        assertTrue(observerCount < pilotCount)
        assertTrue(pilotCount < commanderCount)
        assertTrue(commanderCount < adminCount)
    }
}
