package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MultiFieldMapping

class MultiFieldTargetPathBuilder(
    private val parent: TargetBuilder,
    private val sourcePaths: List<String>,
    private val targetPath: String,
    private val transformation: ((List<Any?>) -> Any)?,
    private val condition: ((Any) -> Boolean)?
) : IMultiFieldTargetPathBuilder {

    override fun end(): TargetBuilder {
        // Default joining with space
        return withSeparator(" ")
    }

    override fun format(template: String): TargetBuilder {
        val formatTransformation: (List<Any?>) -> Any = { values ->
            var result = template
            values.forEachIndexed { index, value ->
                val strValue = extractValue(value)
                result = result.replace("{$index}", strValue)
            }
            DataNode.StringValue(result)
        }

        // If there's already a transformation, use it directly instead of composing
        val finalTransformation = transformation ?: formatTransformation

        val rule = MultiFieldMapping(sourcePaths, targetPath, finalTransformation, condition)
        parent.addRule(rule)
        return parent
    }

    override fun withSeparator(separator: String): TargetBuilder {
        val joinTransformation: (List<Any?>) -> Any = { values ->
            val nonNullValues = values.filterNotNull().map { extractValue(it) }
            DataNode.StringValue(nonNullValues.joinToString(separator))
        }

        val finalTransformation = transformation ?: joinTransformation

        val rule = MultiFieldMapping(sourcePaths, targetPath, finalTransformation, condition)
        parent.addRule(rule)
        return parent
    }

    /**
     * Join multiple field values with a custom separator.
     * This is an alias for withSeparator() for better readability.
     */
    fun joinWith(separator: String): TargetBuilder = withSeparator(separator)

    override fun concatenate(): TargetBuilder = withSeparator("")

    /**
     * Helper method to extract value from DataNode objects.
     */
    private fun extractValue(value: Any?): String {
        if (value == null) return ""

        return when (value) {
            is DataNode.StringValue -> value.value
            is DataNode.NumberValue -> value.value.toString()
            is DataNode.BooleanValue -> value.value.toString()
            is DataNode -> value.asString ?: value.toString()
            else -> value.toString()
        }
    }
}