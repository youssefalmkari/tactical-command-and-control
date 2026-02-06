package com.example.tacticalcommandandcontrol.core.common.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object CoordinateUtils {

    private const val EARTH_RADIUS_METERS = 6_371_000.0

    fun formatLatitude(lat: Double): String {
        val direction = if (lat >= 0) "N" else "S"
        return "${formatDms(abs(lat))} $direction"
    }

    fun formatLongitude(lon: Double): String {
        val direction = if (lon >= 0) "E" else "W"
        return "${formatDms(abs(lon))} $direction"
    }

    fun formatLatLon(lat: Double, lon: Double): String =
        "${formatLatitude(lat)}, ${formatLongitude(lon)}"

    fun formatDecimalDegrees(lat: Double, lon: Double): String =
        String.format(Locale.US, "%.6f, %.6f", lat, lon)

    fun formatAltitude(meters: Double): String =
        String.format(Locale.US, "%.1f m", meters)

    fun formatHeading(degrees: Double): String =
        String.format(Locale.US, "%03.0f\u00B0", (degrees % 360 + 360) % 360)

    fun formatSpeed(metersPerSecond: Double): String =
        String.format(Locale.US, "%.1f m/s", metersPerSecond)

    /**
     * Haversine distance between two lat/lon points in meters.
     */
    fun distanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Bearing from point 1 to point 2 in degrees (0-360).
     */
    fun bearing(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
    ): Double {
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)
        val y = sin(dLon) * cos(rLat2)
        val x = cos(rLat1) * sin(rLat2) - sin(rLat1) * cos(rLat2) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }

    private fun formatDms(decimal: Double): String {
        val degrees = decimal.toInt()
        val minutesDecimal = (decimal - degrees) * 60
        val minutes = minutesDecimal.toInt()
        val seconds = (minutesDecimal - minutes) * 60
        return String.format(Locale.US, "%d\u00B0%02d'%05.2f\"", degrees, minutes, seconds)
    }
}
