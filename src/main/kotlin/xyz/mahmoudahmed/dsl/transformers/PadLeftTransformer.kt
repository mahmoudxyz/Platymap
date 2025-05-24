package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to pad the left side of text with a character.
 */
class PadLeftTransformer(
    private val length: Int,
    private val padChar: Char
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val padded = str.padStart(length, padChar)

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(padded)
            is DataNode -> DataNode.StringValue(padded)
            else -> padded
        }
    }
}
