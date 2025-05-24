package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value is null.
 */
class IsNullTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val isNull = value is DataNode.NullValue
        return DataNode.BooleanValue(isNull)
    }
}