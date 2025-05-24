package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert text to Title Case.
 */
class TitleCaseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        val titleCase = str.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) {
                word[0].uppercase() + word.substring(1).lowercase()
            } else {
                ""
            }
        }

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(titleCase)
            is DataNode -> DataNode.StringValue(titleCase)
            else -> titleCase
        }
    }
}