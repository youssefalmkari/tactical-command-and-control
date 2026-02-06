package com.example.tacticalcommandandcontrol.core.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateUtilsTest {

    @Test
    fun `formatLatitude north`() {
        val result = CoordinateUtils.formatLatitude(37.7749)
        assertTrue(result.endsWith("N"))
        assertTrue(result.contains("37"))
    }

    @Test
    fun `formatLatitude south`() {
        val result = CoordinateUtils.formatLatitude(-33.8688)
        assertTrue(result.endsWith("S"))
        assertTrue(result.contains("33"))
    }

    @Test
    fun `formatLongitude east`() {
        val result = CoordinateUtils.formatLongitude(122.4194)
        assertTrue(result.endsWith("E"))
    }

    @Test
    fun `formatLongitude west`() {
        val result = CoordinateUtils.formatLongitude(-122.4194)
        assertTrue(result.endsWith("W"))
    }

    @Test
    fun `formatDecimalDegrees formats correctly`() {
        val result = CoordinateUtils.formatDecimalDegrees(37.774900, -122.419400)
        assertEquals("37.774900, -122.419400", result)
    }

    @Test
    fun `formatAltitude formats meters`() {
        assertEquals("100.0 m", CoordinateUtils.formatAltitude(100.0))
        assertEquals("0.5 m", CoordinateUtils.formatAltitude(0.5))
    }

    @Test
    fun `formatHeading wraps to 0-360`() {
        assertEquals("045\u00B0", CoordinateUtils.formatHeading(45.0))
        assertEquals("000\u00B0", CoordinateUtils.formatHeading(360.0))
        assertEquals("350\u00B0", CoordinateUtils.formatHeading(-10.0))
    }

    @Test
    fun `formatSpeed formats m per s`() {
        assertEquals("15.5 m/s", CoordinateUtils.formatSpeed(15.5))
    }

    @Test
    fun `distanceMeters same point is zero`() {
        val distance = CoordinateUtils.distanceMeters(37.7749, -122.4194, 37.7749, -122.4194)
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `distanceMeters known distance SF to LA`() {
        // SF (37.7749, -122.4194) to LA (34.0522, -118.2437) ~559 km
        val distance = CoordinateUtils.distanceMeters(37.7749, -122.4194, 34.0522, -118.2437)
        val km = distance / 1000.0
        assertTrue("Distance should be ~559km, was ${km}km", km in 550.0..570.0)
    }

    @Test
    fun `bearing north is approximately 0`() {
        val bearing = CoordinateUtils.bearing(0.0, 0.0, 1.0, 0.0)
        assertTrue("Bearing should be ~0, was $bearing", bearing < 1.0 || bearing > 359.0)
    }

    @Test
    fun `bearing east is approximately 90`() {
        val bearing = CoordinateUtils.bearing(0.0, 0.0, 0.0, 1.0)
        assertEquals(90.0, bearing, 1.0)
    }
}
