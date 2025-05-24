package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert text to snake_case.
 */
class SnakeCaseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        // Replace spaces, dashes and camelCase with underscores
        val snakeCase = str
            .replace(Regex("([a-z])([A-Z])"), "$1_$2") // camelCase to snake_case
            .replace(Regex("[\\s-]+"), "_")
            .lowercase()

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(snakeCase)
            is DataNode -> DataNode.StringValue(snakeCase)
            else -> snakeCase
        }
    }
}
