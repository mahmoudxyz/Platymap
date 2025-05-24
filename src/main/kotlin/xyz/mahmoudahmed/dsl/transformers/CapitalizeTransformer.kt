package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.util.Locale

/**
 * Transformer to capitalize the first letter of each word.
 */
class CapitalizeTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        val capitalized = str.split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(capitalized)
            is DataNode -> DataNode.StringValue(capitalized)
            else -> capitalized
        }
    }
}