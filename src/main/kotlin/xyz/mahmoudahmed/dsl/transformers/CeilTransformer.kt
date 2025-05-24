package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode


/**
 * Transformer to round a number up to the nearest integer.
 */
class CeilTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val ceiled = Math.ceil(number)

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(ceiled)
            is DataNode -> DataNode.NumberValue(ceiled)
            else -> ceiled
        }
    }
}
