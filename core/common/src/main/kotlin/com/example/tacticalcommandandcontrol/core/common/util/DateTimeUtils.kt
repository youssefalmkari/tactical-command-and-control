package com.example.tacticalcommandandcontrol.core.common.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {

    private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US)
    private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale.US)

    fun formatTime(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        TIME_FORMAT.format(instant.atZone(zone))

    fun formatDateTime(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        DATE_TIME_FORMAT.format(instant.atZone(zone))

    fun formatShortDate(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        SHORT_DATE_FORMAT.format(instant.atZone(zone))

    fun formatElapsed(from: Instant, to: Instant = Instant.now()): String {
        val duration = Duration.between(from, to)
        return when {
            duration.seconds < 5 -> "just now"
            duration.seconds < 60 -> "${duration.seconds}s ago"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            else -> "${duration.toDays()}d ago"
        }
    }

    fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()
        return when {
            hours > 0 -> String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    }

    fun isStale(timestamp: Instant, thresholdSeconds: Long = 10): Boolean =
        Duration.between(timestamp, Instant.now()).seconds > thresholdSeconds
}
