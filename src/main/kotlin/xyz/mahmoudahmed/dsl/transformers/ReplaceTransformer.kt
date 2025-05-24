package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to replace occurrences in text.
 */
class ReplaceTransformer(
    private val oldValue: String,
    private val newValue: String
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val replaced = str.replace(oldValue, newValue)

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(replaced)
            is DataNode -> DataNode.StringValue(replaced)
            else -> replaced
        }
    }
}