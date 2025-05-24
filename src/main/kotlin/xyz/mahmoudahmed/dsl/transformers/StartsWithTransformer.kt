package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value starts with a specified prefix.
 */
class StartsWithTransformer(private val prefix: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return DataNode.BooleanValue(str.startsWith(prefix))
    }
}
