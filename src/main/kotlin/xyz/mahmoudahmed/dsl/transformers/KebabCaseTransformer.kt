package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert text to kebab-case.
 */
class KebabCaseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        // Replace spaces, underscores and camelCase with dashes
        val kebabCase = str
            .replace(Regex("([a-z])([A-Z])"), "$1-$2") // camelCase to kebab-case
            .replace(Regex("[\\s_]+"), "-")
            .lowercase()

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(kebabCase)
            is DataNode -> DataNode.StringValue(kebabCase)
            else -> kebabCase
        }
    }
}
