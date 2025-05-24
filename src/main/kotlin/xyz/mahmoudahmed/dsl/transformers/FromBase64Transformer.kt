package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to decode a Base64 string.
 */
class FromBase64Transformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        return try {
            val decodedBytes = java.util.Base64.getDecoder().decode(str)
            val decoded = String(decodedBytes)

            when (value) {
                is DataNode.StringValue -> DataNode.StringValue(decoded)
                else -> decoded
            }
        } catch (e: Exception) {
            value
        }
    }
}