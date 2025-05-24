package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Transformer to convert a date to ISO 8601 date format (yyyy-MM-dd).
 */
class ToIsoDateTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            // Try various date formats
            val date = parseDate(dateStr)
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val formatted = date.format(formatter)

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

    private fun parseDate(dateStr: String): LocalDate {
        // Try common formats
        val formats = listOf(
            "yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd",
            "MMM d, yyyy", "MMMM d, yyyy", "d MMM yyyy", "d MMMM yyyy"
        )

        for (format in formats) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format))
            } catch (e: Exception) {
                // Try next format
            }
        }

        // If we get here, none of the formats worked
        throw IllegalArgumentException("Could not parse date: $dateStr")
    }
}