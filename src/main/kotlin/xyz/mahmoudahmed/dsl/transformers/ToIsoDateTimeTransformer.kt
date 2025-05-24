package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Transformer to convert a date to ISO 8601 date-time format (yyyy-MM-dd'T'HH:mm:ss'Z').
 */
class ToIsoDateTimeTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            // Try various date formats
            val dateTime = parseDateTime(dateStr)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val formatted = dateTime.format(formatter)

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

    private fun parseDateTime(dateStr: String): LocalDateTime {
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
        throw IllegalArgumentException("Could not parse date-time: $dateStr")
    }
}