package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to replace null with a specified value.
 */
class IfNullTransformer(private val replacement: DataNode) : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.NullValue -> replacement
            else -> value
        }
    }
}
