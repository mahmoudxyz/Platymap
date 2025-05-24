package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.DecimalFormat

/**
 * Transformer to format a number according to a pattern.
 */
class FormatNumberTransformer(private val pattern: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val formatter = DecimalFormat(pattern)
        val formatted = formatter.format(number)

        return when (value) {
            is DataNode.NumberValue -> DataNode.StringValue(formatted)
            is DataNode -> DataNode.StringValue(formatted)
            else -> formatted
        }
    }
}
