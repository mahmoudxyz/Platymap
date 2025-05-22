package xyz.mahmoudahmed.dsl.structure

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingExecutionException
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.core.SimpleMapping
import xyz.mahmoudahmed.dsl.util.PathMatcher

/**
 * Rule that nests flat fields into a structured object or array.
 */
class NestingMappingRule(
    private val sourcePattern: String,
    private val targetPath: String,
    private val structureName: String,
    private val fieldNameExtractor: String?,
    private val valueTransformation: ((String, Any) -> Any)?,
    private val isCollection: Boolean,
    private val includeNullValues: Boolean,
    private val strictTypeHandling: Boolean
) : MappingRule {

    override fun apply(context: MappingContext, target: Any) {
        if (target !is DataNode.ObjectNode) {
            throw MappingExecutionException("Target must be a DataNode.ObjectNode")
        }

        try {
            val sourceNode = context.sourceData as? DataNode
                ?: throw MappingExecutionException("Source data must be a DataNode")

            // Find all paths that match the pattern
            val matches = PathMatcher.findMatches(sourceNode, sourcePattern)

            if (matches.isEmpty() && !includeNullValues) {
                // No matches found and we're not including null values, so we don't create the structure
                return
            }

            // Create the nested structure
            if (isCollection) {
                createNestedCollection(matches, target, context)
            } else {
                createNestedObject(matches, target, context)
            }
        } catch (e: Exception) {
            if (e is MappingExecutionException) throw e
            throw MappingExecutionException(
                "Error creating nested structure for pattern '$sourcePattern': ${e.message}",
                e
            )
        }
    }

    private fun createNestedCollection(
        matches: List<PathMatcher.PathMatch>,
        targetNode: DataNode.ObjectNode,
        context: MappingContext
    ) {
        // Create a collection of items
        val arrayNode = DataNode.ArrayNode()

        for (match in matches) {
            val itemNode = DataNode.ObjectNode()

            // Extract the field name to use as a key or include in the item
            val fieldName = extractFieldName(match.path)

            // Transform the value if needed
            val value = transformValue(fieldName, match.value, context)
                ?: if (includeNullValues) DataNode.NullValue else continue

            // Add the field name if specified
            if (!fieldNameExtractor.isNullOrEmpty()) {
                itemNode.properties[fieldNameExtractor] = DataNode.StringValue(fieldName)
            }

            // Add the value
            val valueField = if (!fieldNameExtractor.isNullOrEmpty()) "value" else fieldName

            itemNode.properties[valueField] = value

            // Add to collection
            arrayNode.elements.add(itemNode)
        }

        // Set the array in the target
        setValueInPath(targetNode, "$targetPath.$structureName", arrayNode)
    }

    private fun createNestedObject(
        matches: List<PathMatcher.PathMatch>,
        targetNode: DataNode.ObjectNode,
        context: MappingContext
    ) {
        // Create a single object with all the fields
        val nestedObject = DataNode.ObjectNode()

        for (match in matches) {
            // Extract the field name
            val fieldName = extractFieldName(match.path)

            // Transform the value if needed
            val value = transformValue(fieldName, match.value, context)
                ?: if (includeNullValues) DataNode.NullValue else continue

            // Add to nested object
            nestedObject.properties[fieldName] = value
        }

        // Set the object in the target
        setValueInPath(targetNode, "$targetPath.$structureName", nestedObject)
    }

    private fun transformValue(fieldName: String, value: DataNode, context: MappingContext): DataNode? {
        if (value == DataNode.NullValue && !includeNullValues) {
            return null
        }

        return if (valueTransformation != null) {
            try {
                when (val transformedValue = valueTransformation.invoke(fieldName, value)) {
                    is DataNode -> transformedValue
                    else -> {
                        SimpleMapping.convertToDataNode(transformedValue)
                    }
                }
            } catch (e: Exception) {
                if (strictTypeHandling) {
                    throw MappingExecutionException(
                        "Error transforming value for field '$fieldName': ${e.message}",
                        e
                    )
                }
                value
            }
        } else {
            value
        }
    }

    private fun extractFieldName(path: String): String {
        // Extract the part of the path that matches the wildcard in the pattern
        if (sourcePattern.contains("*")) {
            val prefix = sourcePattern.substring(0, sourcePattern.indexOf("*"))
            val suffix = sourcePattern.substring(sourcePattern.indexOf("*") + 1)

            if (path.startsWith(prefix) && (suffix.isEmpty() || path.endsWith(suffix))) {
                val startIndex = prefix.length
                val endIndex = if (suffix.isEmpty()) path.length else path.length - suffix.length
                return path.substring(startIndex, endIndex)
            }
        }

        // Default: just use the last segment of the path
        val lastDot = path.lastIndexOf('.')
        return if (lastDot >= 0) path.substring(lastDot + 1) else path
    }

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