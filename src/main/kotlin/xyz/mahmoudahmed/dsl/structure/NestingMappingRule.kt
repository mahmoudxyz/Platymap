package xyz.mahmoudahmed.dsl.structure

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingExecutionException
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.core.SimpleMapping

/**
 * Rule that nests flat fields into a structured object or array.
 * Supports pattern matching for grouping related fields.
 */
class NestingMappingRule(
    private val sourcePattern: String,
    private val targetPath: String,
    private val structureName: String,
    private val fieldNameExtractor: String?,
    private val valueTransformation: ((String, Any) -> Any)?,
    private val isCollection: Boolean,
    private val includeNullValues: Boolean = true, // Changed default to true to include null values
    private val strictTypeHandling: Boolean
) : MappingRule {

    override fun apply(context: MappingContext, target: Any) {
        if (target !is DataNode.ObjectNode) {
            throw MappingExecutionException("Target must be a DataNode.ObjectNode")
        }

        try {
            val sourceNode = context.sourceData as? DataNode
                ?: throw MappingExecutionException("Source data must be a DataNode")

            // Extract pattern type and prepare for matching
            val patternType = analyzePatternType(sourcePattern)

            // Find all paths that match the pattern
            val matchedPaths = findAllMatchingPaths(sourceNode, sourcePattern, patternType)

            if (matchedPaths.isEmpty()) {
                return // Nothing to nest
            }

            // Create the appropriate nested structure
            if (isCollection) {
                createNestedCollection(matchedPaths, target, context)
            } else {
                createNestedObject(matchedPaths, target, context)
            }
        } catch (e: Exception) {
            if (e is MappingExecutionException) throw e
            throw MappingExecutionException(
                "Error creating nested structure for pattern '$sourcePattern': ${e.message}",
                e
            )
        }
    }

    /**
     * Represents the type of pattern we're dealing with.
     */
    private enum class PatternType {
        PREFIX_BASED,
        ID_FIELD_BASED,
        PATH_WILDCARD,
        COMPLEX_WILDCARD
    }

    /**
     * Determines the type of pattern we're dealing with.
     */
    private fun analyzePatternType(pattern: String): PatternType {
        return when {
            // ID_FIELD_BASED: patterns like "prefix_*_*" or "item_*_field"
            pattern.contains("_*_") -> PatternType.ID_FIELD_BASED

            // PREFIX_BASED: patterns like "prefix_*"
            pattern.contains("_*") && !pattern.contains("_*_") -> PatternType.PREFIX_BASED

            // PATH_WILDCARD: patterns like "path.*.field"
            pattern.contains(".*") && !pattern.contains("_*") -> PatternType.PATH_WILDCARD

            // COMPLEX_WILDCARD: any other pattern with wildcards
            pattern.contains("*") -> PatternType.COMPLEX_WILDCARD

            // Default to PREFIX_BASED for patterns without wildcards
            else -> PatternType.PREFIX_BASED
        }
    }

    /**
     * Represents a matched path and its components.
     */
    private data class MatchedPathInfo(
        val path: String,           // Full path
        val value: DataNode,        // Value at the path
        val identifier: String,     // Identifier for grouping (e.g., "1" from "item_1_name")
        val fieldName: String       // Field name (e.g., "name" from "item_1_name")
    )

    /**
     * Finds all paths in the source data that match the given pattern.
     */
    private fun findAllMatchingPaths(
        rootNode: DataNode,
        pattern: String,
        patternType: PatternType
    ): List<MatchedPathInfo> {
        val result = mutableListOf<MatchedPathInfo>()

        // Create a regex for path matching
        val patternRegex = createMatchingRegex(pattern)

        // Recursively traverse the data structure
        traverseNode(rootNode, "", patternRegex, patternType, result)

        return result
    }

    /**
     * Creates a regex for matching paths against the pattern.
     */
    private fun createMatchingRegex(pattern: String): Regex {
        // Handle pattern segments
        val segments = pattern.split(".")
        val regexParts = segments.map { segment ->
            if (segment == "*") {
                // Match any path segment
                "[^.]+"
            } else if (segment.contains("*")) {
                // Replace * with a wildcard in the segment
                segment.replace("*", ".*?")
            } else {
                // Exact match for this segment
                segment
            }
        }

        // Join with escaped dots
        val regexPattern = regexParts.joinToString("\\.").replace("[", "\$$").replace("]", "\$$")

        return Regex("^$regexPattern$")
    }

    /**
     * Recursively traverses the node structure to find matching paths.
     */
    private fun traverseNode(
        node: DataNode,
        currentPath: String,
        patternRegex: Regex,
        patternType: PatternType,
        result: MutableList<MatchedPathInfo>
    ) {
        // Check if current path matches the pattern
        if (patternRegex.matches(currentPath) && currentPath.isNotEmpty()) {
            // Extract components based on pattern type
            val (identifier, fieldName) = extractComponents(currentPath, patternType)
            result.add(MatchedPathInfo(currentPath, node, identifier, fieldName))
        }

        // Continue traversal for complex nodes
        when (node) {
            is DataNode.ObjectNode -> {
                for ((key, value) in node.properties) {
                    val newPath = if (currentPath.isEmpty()) key else "$currentPath.$key"
                    traverseNode(value, newPath, patternRegex, patternType, result)
                }
            }
            is DataNode.ArrayNode -> {
                for (i in node.elements.indices) {
                    val newPath = "$currentPath[$i]"
                    traverseNode(node.elements[i], newPath, patternRegex, patternType, result)
                }
            }
            else -> {
                // Leaf node, no further traversal needed
            }
        }
    }

    /**
     * Extracts identifier and field name from a path based on pattern type.
     */
    private fun extractComponents(path: String, patternType: PatternType): Pair<String, String> {
        val lastSegment = path.substringAfterLast('.')

        return when (patternType) {
            PatternType.PREFIX_BASED -> {
                // For patterns like "personal_firstName", extract "firstName"
                if (lastSegment.contains("_")) {
                    val prefix = lastSegment.substringBefore('_')
                    val fieldName = lastSegment.substringAfter('_')
                    Pair("default", fieldName)
                } else {
                    Pair("default", lastSegment)
                }
            }

            PatternType.ID_FIELD_BASED -> {
                // For patterns like "item_1_name", extract "1" and "name"
                val regex = "(\\w+)_(\\d+)_(\\w+)".toRegex()
                val match = regex.find(lastSegment)

                if (match != null && match.groupValues.size > 3) {
                    val id = match.groupValues[2]
                    val field = match.groupValues[3]
                    Pair(id, field)
                } else {
                    // Try alternate format: "prefix_id_field"
                    val parts = lastSegment.split("_")
                    if (parts.size >= 3) {
                        Pair(parts[1], parts[2])
                    } else {
                        Pair("default", lastSegment)
                    }
                }
            }

            PatternType.PATH_WILDCARD -> {
                // For patterns like "data.*.value", use the wildcard segment as identifier
                val segments = path.split(".")
                val wildcardIndex = sourcePattern.split(".").indexOfFirst { it == "*" }

                if (wildcardIndex >= 0 && wildcardIndex < segments.size) {
                    Pair(segments[wildcardIndex], lastSegment)
                } else {
                    Pair("default", lastSegment)
                }
            }

            PatternType.COMPLEX_WILDCARD -> {
                // For more complex patterns, try to extract meaningful parts
                if (lastSegment.contains("_")) {
                    val parts = lastSegment.split("_")
                    if (parts.size >= 3 && parts[1].matches("\\d+".toRegex())) {
                        // Pattern like "prefix_123_field"
                        Pair(parts[1], parts[2])
                    } else if (parts.size >= 2) {
                        // Pattern like "prefix_field"
                        Pair("default", parts[1])
                    } else {
                        Pair("default", lastSegment)
                    }
                } else {
                    Pair("default", lastSegment)
                }
            }
        }
    }

    /**
     * Creates a nested collection structure from the matched paths.
     */
    private fun createNestedCollection(
        matchedPaths: List<MatchedPathInfo>,
        targetNode: DataNode.ObjectNode,
        context: MappingContext
    ) {
        // Group paths by their identifier
        val groupedPaths = matchedPaths.groupBy { it.identifier }

        // Create collection node
        val collectionNode = DataNode.ArrayNode()

        // Sort groups by identifier (numerically if possible)
        val sortedGroups = groupedPaths.toSortedMap(compareBy {
            it.toIntOrNull() ?: Int.MAX_VALUE
        })

        // Process each group
        for ((_, paths) in sortedGroups) {
            val itemNode = DataNode.ObjectNode()

            // Add each field to the item
            for (pathInfo in paths) {
                val propertyName = pathInfo.fieldName
                val value = pathInfo.value

                // Transform the value if needed
                val transformedValue = transformValue(propertyName, value, context)

                // Add to the item, including null values if configured
                if (transformedValue != null || includeNullValues) {
                    itemNode.properties[propertyName] = transformedValue ?: DataNode.NullValue
                }
            }

            // Only add non-empty items
            if (itemNode.properties.isNotEmpty()) {
                collectionNode.elements.add(itemNode)
            }
        }

        // Set the collection in the target
        val targetPathWithName = if (targetPath.isEmpty()) structureName else "$targetPath.$structureName"
        setValueInPath(targetNode, targetPathWithName, collectionNode)
    }

    /**
     * Creates a nested object structure from the matched paths.
     */
    private fun createNestedObject(
        matchedPaths: List<MatchedPathInfo>,
        targetNode: DataNode.ObjectNode,
        context: MappingContext
    ) {
        // Create object node
        val objectNode = DataNode.ObjectNode()

        // Add each field to the object
        for (pathInfo in matchedPaths) {
            val propertyName = pathInfo.fieldName
            val value = pathInfo.value

            // Transform the value if needed
            val transformedValue = transformValue(propertyName, value, context)

            // Add to the object, including null values if configured
            if (transformedValue != null || includeNullValues) {
                objectNode.properties[propertyName] = transformedValue ?: DataNode.NullValue
            }
        }

        // Only set non-empty objects
        if (objectNode.properties.isNotEmpty()) {
            val targetPathWithName = if (targetPath.isEmpty()) structureName else "$targetPath.$structureName"
            setValueInPath(targetNode, targetPathWithName, objectNode)
        }
    }

    /**
     * Transforms a value using the provided transformation function.
     */
    private fun transformValue(propertyName: String, value: DataNode, context: MappingContext): DataNode? {
        if (valueTransformation == null) {
            return value
        }

        try {
            val transformedValue = valueTransformation.invoke(propertyName, value)
            return when (transformedValue) {
                is DataNode -> transformedValue
                else -> SimpleMapping.convertToDataNode(transformedValue)
            }
        } catch (e: Exception) {
            if (strictTypeHandling) {
                throw MappingExecutionException(
                    "Error transforming value for property '$propertyName': ${e.message}",
                    e
                )
            }
            return value
        }
    }

    /**
     * Sets a value at the specified path in the target node.
     */
    private fun setValueInPath(targetNode: DataNode.ObjectNode, path: String, value: DataNode) {
        try {
            SimpleMapping.setValueInDataNode(targetNode, path, value)
        } catch (e: Exception) {
            throw MappingExecutionException(
                "Error setting nested value at path '$path': ${e.message}",
                e
            )
        }
    }
}