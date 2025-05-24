package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Transformer to format a date relative to now (e.g., "2 days ago").
 */
class FormatRelativeDateTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            val date = parseDateTime(dateStr)
            val now = LocalDateTime.now()

            val formatted = when {
                date.isAfter(now) -> formatFuture(date, now)
                else -> formatPast(date, now)
            }

            return when (value) {
                is DataNode.StringValue -> DataNode.StringValue(formatted)
                is DataNode -> DataNode.StringValue(formatted)
                else -> formatted
            }
        } catch (e: Exception) {
            // Return original on error
            return value
        }
    }

    private fun formatPast(date: LocalDateTime, now: LocalDateTime): String {
        val seconds = java.time.Duration.between(date, now).seconds

        return when {
            seconds < 60 -> "just now"
            seconds < 3600 -> "${seconds / 60} minute${if (seconds / 60 != 1L) "s" else ""} ago"
            seconds < 86400 -> "${seconds / 3600} hour${if (seconds / 3600 != 1L) "s" else ""} ago"
            seconds < 2592000 -> "${seconds / 86400} day${if (seconds / 86400 != 1L) "s" else ""} ago"
            seconds < 31536000 -> "${seconds / 2592000} month${if (seconds / 2592000 != 1L) "s" else ""} ago"
            else -> "${seconds / 31536000} year${if (seconds / 31536000 != 1L) "s" else ""} ago"
        }
    }

    private fun formatFuture(date: LocalDateTime, now: LocalDateTime): String {
        val seconds = java.time.Duration.between(now, date).seconds

        return when {
            seconds < 60 -> "in a moment"
            seconds < 3600 -> "in ${seconds / 60} minute${if (seconds / 60 != 1L) "s" else ""}"
            seconds < 86400 -> "in ${seconds / 3600} hour${if (seconds / 3600 != 1L) "s" else ""}"
            seconds < 2592000 -> "in ${seconds / 86400} day${if (seconds / 86400 != 1L) "s" else ""}"
            seconds < 31536000 -> "in ${seconds / 2592000} month${if (seconds / 2592000 != 1L) "s" else ""}"
            else -> "in ${seconds / 31536000} year${if (seconds / 31536000 != 1L) "s" else ""}"
        }
    }

    private fun parseDateTime(dateStr: String): LocalDateTime {
        // Try ISO format first
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            // Continue with other formats
        }

        // Try Unix timestamp
        try {
            val timestamp = dateStr.toLong()
            return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
            )
        } catch (e: Exception) {
            // Continue with other formats
        }

        // Try common formats
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy"
        )

        for (format in formats) {
            try {
                // For date-only formats, append midnight time
                return if (!format.contains("HH:mm:ss")) {
                    val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format))
                    date.atStartOfDay()
                } else {
                    LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format))
                }
            } catch (e: Exception) {
                // Try next format
            }
        }

        // If we get here, none of the formats worked
        throw IllegalArgumentException("Could not parse date-time: $dateStr")
    }
}