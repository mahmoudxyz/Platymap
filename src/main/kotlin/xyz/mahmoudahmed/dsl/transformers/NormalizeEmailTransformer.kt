package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to normalize email address (lowercase and trim).
 */
class NormalizeEmailTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val normalizedEmail = str.trim().lowercase()

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(normalizedEmail)
            is DataNode -> DataNode.StringValue(normalizedEmail)
            else -> normalizedEmail
        }
    }
}