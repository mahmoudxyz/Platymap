package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to provide a default value if the input is null.
 */
class DefaultIfNullTransformer(private val defaultValue: Any) : ValueTransformer {
    override fun transform(value: Any): Any {
        return when {
            value is DataNode.NullValue -> toDataNode(defaultValue)
            value == null -> toDataNode(defaultValue)
            else -> value
        }
    }
}