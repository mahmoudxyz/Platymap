package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to truncate text to a maximum length.
 */
class TruncateTransformer(
    private val maxLength: Int,
    private val suffix: String = "..."
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        val truncated = if (str.length > maxLength) {
            str.take(maxLength) + suffix
        } else {
            str
        }

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(truncated)
            is DataNode -> DataNode.StringValue(truncated)
            else -> truncated
        }
    }
}
