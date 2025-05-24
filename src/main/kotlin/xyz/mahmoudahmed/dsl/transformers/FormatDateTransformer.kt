package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.SimpleDateFormat

/**
 * Transformer to format a date string from one format to another.
 */
class FormatDateTransformer(
    private val inputPattern: String,
    private val outputPattern: String
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            val date = inputFormat.parse(dateStr)
            val formatted = outputFormat.format(date)

            return when (value) {
                is DataNode.StringValue -> DataNode.StringValue(formatted)
                is DataNode -> DataNode.StringValue(formatted)
                else -> formatted
            }
        } catch (e: Exception) {
            return value
        }
    }
}
