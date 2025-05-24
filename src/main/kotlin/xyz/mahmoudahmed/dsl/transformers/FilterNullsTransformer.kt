package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to filter null values from a collection.
 */
class FilterNullsTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> {
                val filtered = DataNode.ArrayNode()
                value.elements.filterNot { it is DataNode.NullValue }.forEach { filtered.add(it) }
                filtered
            }
            else -> value
        }
    }
}
