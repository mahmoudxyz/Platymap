package xyz.mahmoudahmed.dsl.core

import xyz.mahmoudahmed.adapter.DataNode

/**
 * A simple mapping rule that maps a value from source to target.
 */
class SimpleMapping(
    private val sourcePath: String,
    private val targetPath: String,
    private val transformation: ((Any) -> Any)?,
    private val condition: ((Any) -> Boolean)?
) : MappingRule {

    override fun apply(context: MappingContext, target: Any) {
        if (condition != null && !condition.invoke(context.sourceData)) {
            return
        }

        val sourceValue = context.getValueByPath(sourcePath) ?: return

        val targetValue = if (transformation != null) {
            try {
                transformation.invoke(sourceValue)
            } catch (e: Exception) {
                throw MappingExecutionException(
                    "Error applying transformation from $sourcePath to $targetPath: ${e.message}",
                    e
                )
            }
        } else {
            sourceValue
        }

        if (target is DataNode.ObjectNode) {
            try {
                setValueInDataNode(target, targetPath, targetValue)
            } catch (e: Exception) {
                throw MappingExecutionException(
                    "Error setting value from $sourcePath to $targetPath: ${e.message}",
                    e
                )
            }
        } else {
            throw MappingExecutionException(
                "Target must be a DataNode.ObjectNode, but was ${target::class.java.name}"
            )
        }
    }

    companion object {
        /**
         * Sets a value in a DataNode by path.
         *
         * @param target The target DataNode
         * @param path The path where to set the value
         * @param value The value to set
         */
        fun setValueInDataNode(target: DataNode.ObjectNode, path: String, value: Any) {
            val parts = path.split(".")
            var current = target

            // Navigate to the parent node
            for (i in 0 until parts.size - 1) {
                val part = parts[i]
                val child = current.get(part)

                current = if (child == null || child !is DataNode.ObjectNode) {
                    val newNode = DataNode.ObjectNode()
                    current.properties[part] = newNode
                    newNode
                } else {
                    child
                }
            }

            // Set the value on the final node
            val lastPart = parts.last()
            val dataNodeValue = convertToDataNode(value)
            current.properties[lastPart] = dataNodeValue
        }

        /**
         * Converts a value to a DataNode.
         *
         * @param value The value to convert
         * @return The converted DataNode
         */
        fun convertToDataNode(value: Any?): DataNode {
            return when (value) {
                null -> DataNode.NullValue
                is DataNode -> value
                is String -> DataNode.StringValue(value)
                is Number -> DataNode.NumberValue(value)
                is Boolean -> DataNode.BooleanValue(value)
                is Map<*, *> -> {
                    val objNode = DataNode.ObjectNode()
                    for ((k, v) in value) {
                        objNode.properties[k.toString()] = convertToDataNode(v)
                    }
                    objNode
                }
                is Collection<*> -> {
                    val arrayNode = DataNode.ArrayNode()
                    for (item in value) {
                        arrayNode.elements.add(convertToDataNode(item))
                    }
                    arrayNode
                }
                is Array<*> -> {
                    val arrayNode = DataNode.ArrayNode()
                    for (item in value) {
                        arrayNode.elements.add(convertToDataNode(item))
                    }
                    arrayNode
                }
                else -> {
                    // For more complex types, try reflection
                    try {
                        val objNode = DataNode.ObjectNode()
                        val fields = value::class.java.declaredFields
                        for (field in fields) {
                            field.isAccessible = true
                            val fieldValue = field.get(value)
                            objNode.properties[field.name] = convertToDataNode(fieldValue)
                        }
                        objNode
                    } catch (e: Exception) {
                        // For other types, convert to string
                        DataNode.StringValue(value.toString())
                    }
                }
            }
        }
    }
}