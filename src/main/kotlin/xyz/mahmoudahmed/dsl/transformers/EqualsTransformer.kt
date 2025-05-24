package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value equals a specified value.
 */
class EqualsTransformer(private val testValue: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return DataNode.BooleanValue(str == testValue)
    }
}
