package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to encode a string as Base64.
 */
class ToBase64Transformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val encodedBytes = java.util.Base64.getEncoder().encode(str.toByteArray())
        val encoded = String(encodedBytes)

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(encoded)
            else -> encoded
        }
    }
}