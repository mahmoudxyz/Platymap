package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to replace text based on regex pattern.
 */
class ReplaceRegexTransformer(
    private val pattern: Regex,
    private val replacement: String
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val replaced = pattern.replace(str, replacement)

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(replaced)
            is DataNode -> DataNode.StringValue(replaced)
            else -> replaced
        }
    }
}