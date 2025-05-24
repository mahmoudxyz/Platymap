package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to URL encode a string.
 */
class UrlEncodeTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val encoded = java.net.URLEncoder.encode(str, "UTF-8")

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(encoded)
            else -> encoded
        }
    }
}
