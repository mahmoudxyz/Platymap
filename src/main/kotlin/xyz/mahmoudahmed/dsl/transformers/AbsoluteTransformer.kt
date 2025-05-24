package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to get the absolute value of a number.
 */
class AbsoluteTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val abs = Math.abs(number)

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(abs)
            is DataNode -> DataNode.NumberValue(abs)
            else -> abs
        }
    }
}