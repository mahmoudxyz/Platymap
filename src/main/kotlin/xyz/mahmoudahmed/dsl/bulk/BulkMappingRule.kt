package xyz.mahmoudahmed.dsl.bulk

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.util.PathMatcher
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * Rule that maps multiple source fields based on a pattern with improved error handling.
 */
class BulkMappingRule(
    private val sourcePattern: String,
    private val targetPath: String,
    private val exclusions: Set<String>,
    private val inclusions: Set<String>,
    private val transformation: ((String, Any) -> Any)?,
    private val preserveStructure: Boolean
) : MappingRule {

    init {
        require(sourcePattern.isNotBlank()) { "Source pattern cannot be blank" }
        require(targetPath.isNotBlank()) { "Target path cannot be blank" }
    }

    override fun apply(context: MappingContext, target: Any) {
        val targetNode = target as? DataNode.ObjectNode ?: return
        val sourceNode = context.sourceData as? DataNode ?: return

        runCatching {
            processMatches(sourceNode, targetNode)
        }.onFailure { exception ->
            // Log error or handle according to your error strategy
            println("Error applying bulk mapping rule: ${exception.message}")
        }
    }

    private fun processMatches(sourceNode: DataNode, targetNode: DataNode.ObjectNode) {
        val matches = PathMatcher.findMatches(sourceNode, sourcePattern)
        val filteredMatches = filterMatches(matches)

        when {
            preserveStructure -> applyPreservingStructure(filteredMatches, targetNode)
            else -> applyFlatStructure(filteredMatches, targetNode)
        }
    }

    private fun filterMatches(matches: List<PathMatcher.PathMatch>): List<PathMatcher.PathMatch> {
        return matches.filter { match ->
            val path = match.path
            !isExcluded(path) && (inclusions.isEmpty() || isIncluded(path))
        }
    }

    private fun isExcluded(path: String): Boolean {
        return exclusions.any { exclusion ->
            runCatching {
                path.matches(exclusion.replace("*", ".*").toRegex())
            }.getOrDefault(false)
        }
    }

    private fun isIncluded(path: String): Boolean {
        return inclusions.any { inclusion ->
            runCatching {
                path.matches(inclusion.replace("*", ".*").toRegex())
            }.getOrDefault(false)
        }
    }

    private fun applyPreservingStructure(
        matches: List<PathMatcher.PathMatch>,
        targetNode: DataNode.ObjectNode
    ) {
        matches.forEach { match ->
            val relativePath = getRelativePath(match.path, sourcePattern)
            val transformedValue = applyTransformation(match)
            val fullTargetPath = buildTargetPath(relativePath)

            setValueInPath(targetNode, fullTargetPath, transformedValue)
        }
    }

    private fun applyFlatStructure(
        matches: List<PathMatcher.PathMatch>,
        targetNode: DataNode.ObjectNode
    ) {
        matches.forEach { match ->
            val transformedValue = applyTransformation(match)
            val fieldPath = "$targetPath.${match.fieldName}"

            setValueInPath(targetNode, fieldPath, transformedValue)
        }
    }

    private fun applyTransformation(match: PathMatcher.PathMatch): Any {
        return transformation?.let { transform ->
            runCatching {
                transform(match.fieldName, match.value)
            }.getOrElse { match.value }
        } ?: match.value
    }

    private fun buildTargetPath(relativePath: String): String {
        return if (relativePath.isEmpty()) targetPath else "$targetPath.$relativePath"
    }

    private fun getRelativePath(fullPath: String, patternPath: String): String {
        return when {
            patternPath.endsWith(".*") -> {
                val basePath = patternPath.dropLast(2)
                if (fullPath.startsWith(basePath)) {
                    fullPath.drop(basePath.length + 1)
                } else {
                    getFieldName(fullPath)
                }
            }
            patternPath.endsWith(".**") -> {
                val basePath = patternPath.dropLast(3)
                if (fullPath.startsWith(basePath)) {
                    fullPath.drop(basePath.length + 1)
                } else {
                    getFieldName(fullPath)
                }
            }
            else -> getFieldName(fullPath)
        }
    }

    private fun getFieldName(fullPath: String): String {
        return fullPath.substringAfterLast('.', fullPath)
    }

    private fun setValueInPath(targetNode: DataNode.ObjectNode, path: String, value: Any) {
        val parts = path.split(".")
        val navigationPath = parts.dropLast(1)
        val finalKey = parts.last()

        val parentNode = navigationPath.fold(targetNode) { current, part ->
            current.properties.getOrPut(part) { DataNode.ObjectNode() } as? DataNode.ObjectNode
                ?: throw IllegalStateException("Cannot navigate through non-object node at path: $part")
        }

        parentNode.properties[finalKey] = value.toDataNode()
    }

    private fun Any?.toDataNode(): DataNode = DataNodeConverter.convert(this)
}
