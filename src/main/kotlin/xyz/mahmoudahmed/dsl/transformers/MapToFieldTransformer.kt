package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to map an array of objects to a specific field.
 */
class MapToFieldTransformer(private val fieldName: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> {
                val mapped = DataNode.ArrayNode()
                value.elements.forEach { element ->
                    if (element is DataNode.ObjectNode) {
                        element.properties[fieldName]?.let { mapped.add(it) }
                    }
                }
                mapped
            }
            else -> value
        }
    }
}