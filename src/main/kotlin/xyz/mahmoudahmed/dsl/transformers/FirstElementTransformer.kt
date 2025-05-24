package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to get the first element of a collection.
 */
class FirstElementTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> value.elements.firstOrNull() ?: DataNode.NullValue
            is List<*> -> value.firstOrNull()?.let { toDataNode(it) } ?: DataNode.NullValue
            is Array<*> -> value.firstOrNull()?.let { toDataNode(it) } ?: DataNode.NullValue
            else -> value
        }
    }
}
