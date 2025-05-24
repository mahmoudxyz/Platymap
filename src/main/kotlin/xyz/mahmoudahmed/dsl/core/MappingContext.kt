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
     * Supports literal values, variable references, and path expressions.
     *
     * @param path The path to the value
     * @return The value at the specified path or null if not found
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
                // Path in source data
                extractValueByPath(sourceData, path)
            }
        }
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
