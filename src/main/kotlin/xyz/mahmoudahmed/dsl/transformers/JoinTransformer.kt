package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to join an array into a string.
 */
class JoinTransformer(private val delimiter: String = ", ") : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> {
                val joined = value.elements.joinToString(delimiter) {
                    when (it) {
                        is DataNode.StringValue -> it.value
                        is DataNode.NumberValue -> it.value.toString()
                        is DataNode.BooleanValue -> it.value.toString()
                        else -> ""
                    }
                }
                DataNode.StringValue(joined)
            }
            is List<*> -> {
                val joined = value.joinToString(delimiter) { it.toString() }
                DataNode.StringValue(joined)
            }
            is Array<*> -> {
                val joined = value.joinToString(delimiter) { it.toString() }
                DataNode.StringValue(joined)
            }
            else -> value
        }
    }
}