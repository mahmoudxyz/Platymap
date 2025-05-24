package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Base class for date manipulation transformers.
 */
abstract class DateManipulationTransformer(
    private val inputPattern: String = "yyyy-MM-dd"
) : ValueTransformer {
    abstract fun manipulateDate(date: LocalDate): LocalDate

    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            val formatter = DateTimeFormatter.ofPattern(inputPattern)
            val date = LocalDate.parse(dateStr, formatter)
            val newDate = manipulateDate(date)
            val formatted = newDate.format(formatter)

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
}
