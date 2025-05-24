package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to mask a string, showing only the last N characters.
 */
class MaskTransformer(
    private val visibleChars: Int = 4,
    private val maskChar: Char = '*'
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        val masked = if (str.length > visibleChars) {
            maskChar.toString().repeat(str.length - visibleChars) + str.takeLast(visibleChars)
        } else {
            str
        }

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(masked)
            is DataNode -> DataNode.StringValue(masked)
            else -> masked
        }
    }
}
