package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to get the last element of a collection.
 */
class LastElementTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> value.elements.lastOrNull() ?: DataNode.NullValue
            is List<*> -> value.lastOrNull()?.let { toDataNode(it) } ?: DataNode.NullValue
            is Array<*> -> value.lastOrNull()?.let { toDataNode(it) } ?: DataNode.NullValue
            else -> value
        }
    }
}