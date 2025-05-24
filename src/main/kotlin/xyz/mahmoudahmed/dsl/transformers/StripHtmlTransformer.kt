package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to strip HTML tags from text.
 */
class StripHtmlTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val stripped = str.replace(Regex("<[^>]*>"), "")

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(stripped)
            is DataNode -> DataNode.StringValue(stripped)
            else -> stripped
        }
    }
}
