package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value ends with a specified suffix.
 */
class EndsWithTransformer(private val suffix: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return DataNode.BooleanValue(str.endsWith(suffix))
    }
}