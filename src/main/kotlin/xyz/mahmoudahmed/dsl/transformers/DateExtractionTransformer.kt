package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Base class for date extraction transformers.
 */
abstract class DateExtractionTransformer(
    private val inputPattern: String = "yyyy-MM-dd"
) : ValueTransformer {
    abstract fun extractFromDate(date: LocalDate): Int

    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            val formatter = DateTimeFormatter.ofPattern(inputPattern)
            val date = LocalDate.parse(dateStr, formatter)
            val extracted = extractFromDate(date)

            return when (value) {
                is DataNode.StringValue -> DataNode.NumberValue(extracted)
                is DataNode -> DataNode.NumberValue(extracted)
                else -> extracted
            }
        } catch (e: Exception) {
            // Return 0 on error
            return when (value) {
                is DataNode.StringValue -> DataNode.NumberValue(0)
                is DataNode -> DataNode.NumberValue(0)
                else -> 0
            }
        }
    }
}