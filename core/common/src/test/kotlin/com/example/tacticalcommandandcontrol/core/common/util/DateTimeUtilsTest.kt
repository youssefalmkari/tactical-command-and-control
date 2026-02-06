package com.example.tacticalcommandandcontrol.core.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class DateTimeUtilsTest {

    private val fixedZone = ZoneId.of("UTC")

    @Test
    fun `formatTime returns HH mm ss`() {
        val instant = Instant.parse("2025-06-15T14:30:45Z")
        val result = DateTimeUtils.formatTime(instant, fixedZone)
        assertEquals("14:30:45", result)
    }

    @Test
    fun `formatDateTime returns full timestamp`() {
        val instant = Instant.parse("2025-06-15T14:30:45Z")
        val result = DateTimeUtils.formatDateTime(instant, fixedZone)
        assertEquals("2025-06-15 14:30:45", result)
    }

    @Test
    fun `formatElapsed just now`() {
        val now = Instant.now()
        val result = DateTimeUtils.formatElapsed(now, now.plusSeconds(2))
        assertEquals("just now", result)
    }

    @Test
    fun `formatElapsed seconds ago`() {
        val now = Instant.now()
        val result = DateTimeUtils.formatElapsed(now, now.plusSeconds(30))
        assertEquals("30s ago", result)
    }

    @Test
    fun `formatElapsed minutes ago`() {
        val now = Instant.now()
        val result = DateTimeUtils.formatElapsed(now, now.plusSeconds(300))
        assertEquals("5m ago", result)
    }

    @Test
    fun `formatElapsed hours ago`() {
        val now = Instant.now()
        val result = DateTimeUtils.formatElapsed(now, now.plusSeconds(7200))
        assertEquals("2h ago", result)
    }

    @Test
    fun `formatElapsed days ago`() {
        val now = Instant.now()
        val result = DateTimeUtils.formatElapsed(now, now.plusSeconds(172800))
        assertEquals("2d ago", result)
    }

    @Test
    fun `formatDuration hours minutes seconds`() {
        val duration = Duration.ofSeconds(3723) // 1h 2m 3s
        assertEquals("1:02:03", DateTimeUtils.formatDuration(duration))
    }

    @Test
    fun `formatDuration minutes and seconds only`() {
        val duration = Duration.ofSeconds(125) // 2m 5s
        assertEquals("2:05", DateTimeUtils.formatDuration(duration))
    }

    @Test
    fun `isStale returns false for recent timestamp`() {
        val recent = Instant.now().minusSeconds(5)
        assertFalse(DateTimeUtils.isStale(recent, thresholdSeconds = 10))
    }

    @Test
    fun `isStale returns true for old timestamp`() {
        val old = Instant.now().minusSeconds(30)
        assertTrue(DateTimeUtils.isStale(old, thresholdSeconds = 10))
    }
}
