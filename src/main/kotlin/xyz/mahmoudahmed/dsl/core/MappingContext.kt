package xyz.mahmoudahmed.dsl.core

import xyz.mahmoudahmed.adapter.DataNode
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

/**
 * The mapping context holds the source data and variables during mapping execution.
 * It provides access to values by path and manages the variable scope.
 */
class MappingContext(val sourceData: Any) {
    private val variables = mutableMapOf<String, Any>()
    private val pathCache = ConcurrentHashMap<String, PathAccessor>()
    val properties = mutableMapOf<String, Any>()

    /**
     * Sets a variable in the current context.
     *
     * @param name The variable name
     * @param value The variable value
     */
    fun setVariable(name: String, value: Any) {
        variables[name] = value
    }

    /**
     * Removes a variable.
     */
    fun removeVariable(name: String) {
        variables.remove(name)
    }

    /**
     * Gets a variable from the current context.
     *
     * @param name The variable name
     * @return The variable value or null if not found
     */
    fun getVariable(name: String): Any? {
        return variables[name]
    }

    /**
     * Gets all variables in the current context.
     *
     * @return An immutable map of all variables
     */
    fun getVariables(): Map<String, Any> {
        return variables.toMap()
    }

    /**
     * Gets a value from the source data by path.
     * Supports literal values, variable references, path expressions, and arithmetic.
     *
     * @param path The path to the value
     * @return The value at the specified path or null if not found
     */
    fun getValueByPath(path: String): Any? {
        // Check if it's an arithmetic expression
        if (path.contains(" * ") || path.contains(" + ") || path.contains(" - ") || path.contains(" / ")) {
            return evaluateExpression(path)
        }

        return when {
            path.startsWith("'") && path.endsWith("'") -> {
                // Literal value
                path.substring(1, path.length - 1)
            }
            path.startsWith("$") -> {
                // Variable reference, possibly with a nested path
                val dotIndex = path.indexOf('.')
                if (dotIndex > 0) {
                    // Handle variable reference with a nested path (e.g., $item.productName)
                    val varName = path.substring(1, dotIndex)
                    val nestedPath = path.substring(dotIndex + 1)
                    val varValue = variables[varName] ?: return null

                    // Extract the nested property from the variable
                    extractValueByPath(varValue, nestedPath)
                } else {
                    // Simple variable reference (e.g., $item)
                    variables[path.substring(1)]
                }
            }
            else -> {
                // Path in source data
                extractValueByPath(sourceData, path)
            }
        }
    }


    /**
     * Evaluates an arithmetic expression.
     * Supports basic operations: +, -, *, /
     *
     * @param expression The expression to evaluate
     * @return The result of the evaluation
     */
    private fun evaluateExpression(expression: String): Any? {
        try {
            // Split on spaces to separate operands and operators
            val parts = expression.trim().split(" ")

            if (parts.size < 3 || parts.size % 2 == 0) {
                throw IllegalArgumentException("Invalid expression format: $expression")
            }

            // Start with the first operand
            var result = getOperandValue(parts[0])

            // Process each operator and operand pair
            for (i in 1 until parts.size step 2) {
                val operator = parts[i]
                val operand = getOperandValue(parts[i + 1])

                // Extract numeric values, handling DataNode types
                val leftValue = extractNumericValue(result)
                val rightValue = extractNumericValue(operand)

                if (leftValue != null && rightValue != null) {
                    result = when (operator) {
                        "+" -> leftValue + rightValue
                        "-" -> leftValue - rightValue
                        "*" -> leftValue * rightValue
                        "/" -> {
                            if (rightValue == 0.0) {
                                throw ArithmeticException("Division by zero")
                            }
                            leftValue / rightValue
                        }
                        else -> throw IllegalArgumentException("Unsupported operator: $operator")
                    }

                    // Convert the result to a DataNode.NumberValue
                    result = DataNode.NumberValue(result as Number)
                } else {
                    throw IllegalArgumentException(
                        "Cannot perform arithmetic on non-numeric values: $result and $operand"
                    )
                }
            }



            return result
        } catch (e: Exception) {
            throw MappingExecutionException("Error evaluating expression: $expression", e)
        }
    }

    /**
     * Extracts a numeric value from various types, including DataNode.
     */
    private fun extractNumericValue(value: Any?): Double? {
        return when (value) {
            is DataNode.NumberValue -> value.value.toDouble()
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    /**
     * Gets the value of an operand, which can be a literal, variable, or path.
     */
    private fun getOperandValue(operand: String): Any? {
        val value = getValueByPath(operand.trim())

        if (value == null) {
            // Try to parse as a numeric literal
            val numericValue = operand.toDoubleOrNull()
            return if (numericValue != null) {
                DataNode.NumberValue(numericValue)
            } else {
                throw IllegalArgumentException("Cannot resolve operand: $operand")
            }
        }

        return value
    }





    fun setProperty(key: String, value: Any) {
        properties[key] = value
    }

    fun getProperty(key: String): Any? {
        return properties[key]
    }

    /**
     * Extracts a value from source data using a path expression.
     *
     * @param source The source object
     * @param path The path to the value
     * @return The extracted value or null if not found
     */
    private fun extractValueByPath(source: Any, path: String): Any? {
        if (path.isEmpty()) return source

        try {
            // Use cached path accessor if available
            val accessor = pathCache.computeIfAbsent(path) { PathAccessor.create(it) }
            return accessor.getValue(source)
        } catch (e: Exception) {
            throw MappingExecutionException("Failed to extract value", path)
        }
    }


    // Add this method to the MappingContext class

    /**
     * Sets a value at the specified path in the target object.
     * Creates intermediate objects/maps as needed.
     *
     * @param path The path where to set the value (e.g., "receipt.lines")
     * @param value The value to set
     * @param target The target object to modify
     */
    fun setValueByPath(path: String, value: Any?, target: Any) {
        if (path.isEmpty()) {
            throw IllegalArgumentException("Path cannot be empty")
        }

        val pathParts = path.split(".")
        var current = target

        // Navigate to the parent of the final property
        for (i in 0 until pathParts.size - 1) {
            val part = pathParts[i]
            current = when (current) {
                is MutableMap<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    val map = current as MutableMap<String, Any>

                    // Create intermediate map if it doesn't exist
                    if (!map.containsKey(part)) {
                        map[part] = mutableMapOf<String, Any>()
                    }
                    map[part]!!
                }
                is DataNode.ObjectNode -> {
                    // For DataNode.ObjectNode, we need to handle it differently
                    var child = current.get(part)
                    if (child == null) {
                        child = DataNode.ObjectNode(mutableMapOf())
                        current.set(part, child)
                    }
                    child
                }
                else -> {
                    throw IllegalArgumentException("Cannot navigate path '$path' at segment '$part' - current object is not a map or object node: ${current::class.simpleName}")
                }
            }
        }

        // Set the final value
        val finalKey = pathParts.last()
        when (current) {
            is MutableMap<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                val map = current as MutableMap<String, Any>
                if (value != null) {
                    map[finalKey] = value
                } else {
                    map.remove(finalKey)
                }
            }
            is DataNode.ObjectNode -> {
                if (value != null) {
                    current[finalKey] = value as DataNode
                } else {
                    current[finalKey] = DataNode.NullValue
                }
            }
            else -> {
                throw IllegalArgumentException("Cannot set value at path '$path' - final target is not a map or object node: ${current::class.simpleName}")
            }
        }
    }

    /**
     * Abstract class for accessing values by path.
     */
    private abstract class PathAccessor {
        abstract fun getValue(source: Any): Any?

        companion object {
            /**
             * Creates a path accessor for the given path.
             */
            fun create(path: String): PathAccessor {
                val segments = parsePathSegments(path)
                return CompositePathAccessor(segments)
            }

            /**
             * Parses a path into segments.
             */
            private fun parsePathSegments(path: String): List<PathSegment> {
                val result = mutableListOf<PathSegment>()
                val parts = path.split(".")

                for (part in parts) {
                    if (part.contains("[") && part.endsWith("]")) {
                        // Array indexing: items[0]
                        val bracketPos = part.indexOf("[")
                        val propertyName = part.substring(0, bracketPos)
                        val indexStr = part.substring(bracketPos + 1, part.length - 1)

                        result.add(PropertyPathSegment(propertyName))
                        result.add(IndexPathSegment(indexStr.toInt()))
                    } else {
                        // Regular property access
                        result.add(PropertyPathSegment(part))
                    }
                }

                return result
            }
        }
    }

    /**
     * Composite path accessor that applies multiple segments in sequence.
     */
    private class CompositePathAccessor(private val segments: List<PathSegment>) : PathAccessor() {
        override fun getValue(source: Any): Any? {
            var current: Any? = source

            for (segment in segments) {
                current = segment.access(current ?: return null)
                if (current == null) return null
            }

            return current
        }
    }

    /**
     * Interface for path segments.
     */
    private interface PathSegment {
        fun access(source: Any): Any?
    }

    /**
     * Path segment for accessing a property by name.
     */
    private class PropertyPathSegment(private val name: String) : PathSegment {
        override fun access(source: Any): Any? {
            return when (source) {
                is DataNode.ObjectNode -> source.get(name)
                else -> {
                    // Try reflection for regular objects
                    try {
                        val field = findField(source.javaClass, name)
                        field.isAccessible = true
                        field.get(source)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }

        private fun findField(clazz: Class<*>, fieldName: String): Field {
            try {
                return clazz.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                val superClass = clazz.superclass
                if (superClass != null) {
                    return findField(superClass, fieldName)
                }
                throw e
            }
        }
    }

    /**
     * Path segment for accessing an array or list element by index.
     */
    private class IndexPathSegment(private val index: Int) : PathSegment {
        override fun access(source: Any): Any? {
            return when (source) {
                is DataNode.ArrayNode -> {
                    if (index >= 0 && index < source.elements.size) {
                        source.elements[index]
                    } else {
                        null
                    }
                }
                is List<*> -> {
                    if (index >= 0 && index < source.size) {
                        source[index]
                    } else {
                        null
                    }
                }
                is Array<*> -> {
                    if (index >= 0 && index < source.size) {
                        source[index]
                    } else {
                        null
                    }
                }
                else -> null
            }
        }
    }
}
