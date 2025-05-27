package xyz.mahmoudahmed.dsl.core

import xyz.mahmoudahmed.adapter.DataNode

/**
 * A mapping rule that combines multiple source fields into a single target field.
 */
class MultiFieldMapping(
    private val sourcePaths: List<String>,
    private val targetPath: String,
    private val transformation: (List<Any?>) -> Any?,
    private val condition: ((Any) -> Boolean)?
) : MappingRule {
    override fun apply(context: MappingContext, target: Any) {
        // Check condition if present
        if (condition != null && condition?.let { it(context.sourceData) } == true) {
            return
        }

        // Collect values from all source paths
        val sourceValues = sourcePaths.map { path -> context.getValueByPath(path) }

        // Apply transformation if provided
        val targetValue = if (transformation != null) {
            try {
                transformation?.let { it(sourceValues) }
            } catch (e: Exception) {
                throw MappingExecutionException(
                    "Error applying multi-field transformation to $targetPath: ${e.message}",
                    e
                )
            }
        } else {
            // Default behavior: join with space
            val nonNullValues = sourceValues.filterNotNull().map {
                when (it) {
                    is DataNode.StringValue -> it.value
                    is DataNode.NumberValue -> it.value.toString()
                    is DataNode.BooleanValue -> it.value.toString()
                    else -> it.toString()
                }
            }
            DataNode.StringValue(nonNullValues.joinToString(" "))
        }

        // Set the result in the target
        if (target is DataNode.ObjectNode) {
            try {
                if (targetValue != null) {
                    SimpleMapping.setValueInDataNode(target, targetPath, targetValue)
                }
            } catch (e: Exception) {
                throw MappingExecutionException(
                    "Error setting multi-field value to $targetPath: ${e.message}",
                    e
                )
            }
        } else {
            throw MappingExecutionException(
                "Target must be a DataNode.ObjectNode, but was ${target::class.java.name}"
            )
        }
    }
}