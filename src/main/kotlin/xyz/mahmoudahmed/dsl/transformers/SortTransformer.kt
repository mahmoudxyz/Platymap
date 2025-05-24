package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to sort a collection.
 */
class SortTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> {
                val sorted = DataNode.ArrayNode()
                value.elements.sortedBy {
                    when (it) {
                        is DataNode.StringValue -> it.value
                        is DataNode.NumberValue -> it.value.toString()
                        else -> it.toString()
                    }
                }.forEach { sorted.add(it) }
                sorted
            }
            else -> value
        }
    }
}