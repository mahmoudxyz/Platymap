package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to multiply a number by a factor.
 */
class MultiplyTransformer(private val factor: Number) : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val result = number * factor.toDouble()

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(result)
            is DataNode -> DataNode.NumberValue(result)
            else -> result
        }
    }
}