package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to round a number down to the nearest integer.
 */
class FloorTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val floored = Math.floor(number)

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(floored)
            is DataNode -> DataNode.NumberValue(floored)
            else -> floored
        }
    }
}