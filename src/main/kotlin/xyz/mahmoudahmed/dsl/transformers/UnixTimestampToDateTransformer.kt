package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.SimpleDateFormat
import java.util.*

/**
 * Transformer to convert a Unix timestamp to a date string.
 */
class UnixTimestampToDateTransformer(
    private val pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val timestamp = extractNumberValue(value).toLong()

        try {
            val date = Date(timestamp * 1000)
            val format = SimpleDateFormat(pattern)
            val formatted = format.format(date)

            return when (value) {
                is DataNode.NumberValue -> DataNode.StringValue(formatted)
                is DataNode -> DataNode.StringValue(formatted)
                else -> formatted
            }
        } catch (e: Exception) {
            // Return empty string on error
            return when (value) {
                is DataNode.NumberValue -> DataNode.StringValue("")
                is DataNode -> DataNode.StringValue("")
                else -> ""
            }
        }
    }
}
