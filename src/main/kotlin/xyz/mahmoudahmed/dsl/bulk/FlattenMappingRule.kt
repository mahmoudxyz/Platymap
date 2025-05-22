package xyz.mahmoudahmed.dsl.bulk

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * Rule that flattens nested structures with improved error handling.
 */
class FlattenMappingRule(
    private val sourcePattern: String,
    private val targetPath: String,
    private val exclusions: Set<String>,
    private val inclusions: Set<String>,
    private val transformation: ((String, Any) -> Any)?,
    private val prefix: String
) : MappingRule {

    init {
        require(sourcePattern.isNotBlank()) { "Source pattern cannot be blank" }
        require(targetPath.isNotBlank()) { "Target path cannot be blank" }
    }

    override fun apply(context: MappingContext, target: Any) {
        val targetNode = target as? DataNode.ObjectNode ?: return
        val sourceNode = context.sourceData as? DataNode ?: return

        runCatching {
            processFlattening(sourceNode, targetNode)
        }.onFailure { exception ->
            println("Error applying flatten mapping rule: ${exception.message}")
        }
    }

    private fun processFlattening(sourceNode: DataNode, targetNode: DataNode.ObjectNode) {
        val objectToFlatten = findObjectToFlatten(sourceNode, sourcePattern)
                as? DataNode.ObjectNode ?: return

        val targetParent = getOrCreateTargetParent(targetNode, targetPath)
        flattenObject(objectToFlatten, targetParent, prefix, "")
    }

    private fun findObjectToFlatten(sourceNode: DataNode, pattern: String): DataNode? {
        if (pattern.isEmpty()) return sourceNode

        return pattern.split(".").fold(sourceNode as DataNode?) { current, part ->
            (current as? DataNode.ObjectNode)?.get(part)
        }
    }

    private fun getOrCreateTargetParent(root: DataNode.ObjectNode, path: String): DataNode.ObjectNode {
        if (path.isEmpty()) return root

        return path.split(".").fold(root) { current, part ->
            current.properties.getOrPut(part) { DataNode.ObjectNode() } as DataNode.ObjectNode
        }
    }

    private fun flattenObject(
        source: DataNode.ObjectNode,
        target: DataNode.ObjectNode,
        keyPrefix: String,
        keyPath: String
    ) {
        source.properties.forEach { (key, value) ->
            val fullPath = if (keyPath.isEmpty()) key else "$keyPath.$key"

            if (!isPathAllowed(fullPath)) return@forEach

            when (value) {
                is DataNode.ObjectNode -> {
                    flattenObject(value, target, keyPrefix, fullPath)
                }
                else -> {
                    val flatKey = keyPrefix + fullPath.replace('.', '_')
                    val transformedValue = applyTransformation(fullPath, value)
                    target.properties[flatKey] = transformedValue.toDataNode()
                }
            }
        }
    }

    private fun isPathAllowed(path: String): Boolean {
        val isExcluded = exclusions.any { exclusion ->
            runCatching {
                path.matches(exclusion.replace("*", ".*").toRegex())
            }.getOrDefault(false)
        }

        if (isExcluded) return false

        if (inclusions.isEmpty()) return true

        return inclusions.any { inclusion ->
            runCatching {
                path.matches(inclusion.replace("*", ".*").toRegex())
            }.getOrDefault(false)
        }
    }

    private fun applyTransformation(path: String, value: Any): Any {
        return transformation?.let { transform ->
            runCatching {
                transform(path, value)
            }.getOrElse { value }
        } ?: value
    }

    private fun Any?.toDataNode(): DataNode = DataNodeConverter.convert(this)
}