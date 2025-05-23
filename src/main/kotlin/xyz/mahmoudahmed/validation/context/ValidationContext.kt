package xyz.mahmoudahmed.validation.context

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Context for validation operations, providing access to data and variables.
 */
class ValidationContext(val data: Any) {
    private val variables = mutableMapOf<String, Any?>()

    /**
     * Gets a value from the data by path.
     */
    fun getValueByPath(path: String): Any? {
        return when {
            path.startsWith("'") && path.endsWith("'") -> {
                // Literal value
                path.substring(1, path.length - 1)
            }
            path.startsWith("$") -> {
                // Variable reference
                variables[path.substring(1)]
            }
            else -> {
                // Path in data
                extractValueByPath(data, path)
            }
        }
    }

    /**
     * Sets a variable in the context.
     */
    fun setVariable(name: String, value: Any?) {
        variables[name] = value
    }

    /**
     * Gets a variable from the context.
     */
    fun getVariable(name: String): Any? {
        return variables[name]
    }

    /**
     * Extracts a value from data using a path expression.
     */
    private fun extractValueByPath(source: Any, path: String): Any? {
        if (path.isEmpty()) return source

        val parts = path.split(".")
        var current: Any? = source

        for (part in parts) {
            if (current == null) return null

            // Handle array indexing
            if (part.contains("[") && part.endsWith("]")) {
                val bracketPos = part.indexOf("[")
                val fieldName = part.substring(0, bracketPos)
                val indexStr = part.substring(bracketPos + 1, part.length - 1)

                current = when (current) {
                    is DataNode.ObjectNode -> current.get(fieldName)
                    is Map<*, *> -> (current as Map<String, Any?>)[fieldName]
                    else -> null
                }

                if (current == null) return null

                // Access array element by index
                val index = indexStr.toIntOrNull() ?: return null
                current = when (current) {
                    is DataNode.ArrayNode -> {
                        if (index >= 0 && index < current.elements.size)
                            current.elements[index] else null
                    }
                    is List<*> -> {
                        if (index >= 0 && index < current.size)
                            current[index] else null
                    }
                    is Array<*> -> {
                        if (index >= 0 && index < current.size)
                            current[index] else null
                    }
                    else -> null
                }
            } else {
                // Regular property access
                current = when (current) {
                    is DataNode.ObjectNode -> current.get(part)
                    is Map<*, *> -> (current as Map<String, Any?>)[part]
                    else -> {
                        // Try reflection for other object types
                        try {
                            val field = current!!::class.java.getDeclaredField(part)
                            field.isAccessible = true
                            field.get(current)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
        }

        return current
    }
}
