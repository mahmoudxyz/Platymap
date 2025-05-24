package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to URL decode a string.
 */
class UrlDecodeTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        return try {
            val decoded = java.net.URLDecoder.decode(str, "UTF-8")

            when (value) {
                is DataNode.StringValue -> DataNode.StringValue(decoded)
                else -> decoded
            }
        } catch (e: Exception) {
            // Return the original value if decoding fails
            value
        }
    }
}