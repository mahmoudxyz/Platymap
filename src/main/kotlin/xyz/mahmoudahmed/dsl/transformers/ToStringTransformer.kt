package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a value to a string.
 */
class ToStringTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = when (value) {
            is DataNode.StringValue -> value.value
            is DataNode.NumberValue -> value.value.toString()
            is DataNode.BooleanValue -> value.value.toString()
            is DataNode.NullValue -> ""
            is DataNode.ArrayNode -> value.elements.joinToString(", ") {
                when (it) {
                    is DataNode.StringValue -> it.value
                    is DataNode.NumberValue -> it.value.toString()
                    is DataNode.BooleanValue -> it.value.toString()
                    else -> ""
                }
            }
            is DataNode.ObjectNode -> "[Object]"
            else -> value.toString()
        }

        return DataNode.StringValue(str)
    }
}
