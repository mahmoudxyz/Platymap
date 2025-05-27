package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

class MultiplyBy(private val multiplier: Double) : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val result =  number * multiplier

        println()
        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(result)
            is DataNode -> DataNode.NumberValue(result)
            else -> result
        }
    }
}