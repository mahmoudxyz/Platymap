package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to split a string into an array.
 */
class SplitTransformer(private val delimiter: String = ",") : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        val parts = str.split(delimiter).map { it.trim() }
        val array = DataNode.ArrayNode()
        parts.forEach { array.add(DataNode.StringValue(it)) }

        return array
    }
}