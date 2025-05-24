package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to negate a boolean value.
 */
class NegateTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val bool = extractBooleanValue(value)
        return when (value) {
            is DataNode.BooleanValue -> DataNode.BooleanValue(!bool)
            is DataNode -> DataNode.BooleanValue(!bool)
            else -> !bool
        }
    }
}