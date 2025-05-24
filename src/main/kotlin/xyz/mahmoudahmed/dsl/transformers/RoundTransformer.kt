package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to round a number to the nearest integer.
 */
class RoundTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val rounded = Math.round(number).toDouble()

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(rounded)
            is DataNode -> DataNode.NumberValue(rounded)
            else -> rounded
        }
    }
}