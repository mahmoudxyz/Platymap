package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert text to camelCase.
 */
class CamelCaseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        val words = str.split(Regex("[\\s_-]+"))
        val camelCase = words.mapIndexed { index, word ->
            if (index == 0) {
                word.lowercase()
            } else if (word.isNotEmpty()) {
                word[0].uppercase() + word.substring(1).lowercase()
            } else {
                ""
            }
        }.joinToString("")

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(camelCase)
            is DataNode -> DataNode.StringValue(camelCase)
            else -> camelCase
        }
    }
}