package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.SimpleDateFormat

/**
 * Transformer to convert a date string to Unix timestamp.
 */
class DateToUnixTimestampTransformer(
    private val pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val dateStr = extractStringValue(value)

        try {
            val format = SimpleDateFormat(pattern)
            val date = format.parse(dateStr)
            val timestamp = date.time / 1000

            return when (value) {
                is DataNode.StringValue -> DataNode.NumberValue(timestamp)
                is DataNode -> DataNode.NumberValue(timestamp)
                else -> timestamp
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