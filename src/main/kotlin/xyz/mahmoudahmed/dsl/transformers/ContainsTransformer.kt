package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value contains a specified substring.
 */
class ContainsTransformer(private val substring: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return DataNode.BooleanValue(str.contains(substring))
    }
}