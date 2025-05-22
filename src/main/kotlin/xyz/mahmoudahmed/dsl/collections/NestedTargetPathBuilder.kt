package xyz.mahmoudahmed.dsl.collections

/**
 * Builder for finalizing a nested mapping.
 */
class NestedTargetPathBuilder(
    private val parent: NestedMappingBuilder,
    private val sourcePath: String,
    private val targetPath: String,
    private val transformation: ((Any) -> Any)?
) {
    /**
     * Finishes the mapping configuration.
     *
     * @return The parent nested mapping builder
     */
    fun end(): NestedMappingBuilder {
        val fullTargetPath = buildTargetPath()
        val rule = NestedSimpleMapping<Any>(
            sourcePath,
            fullTargetPath,
            transformation,
            null
        )
        parent.addNestedRule(rule)
        return parent
    }

    private fun buildTargetPath(): String {
        return if (targetPath.startsWith(".")) {
            parent.targetCollectionPath + targetPath
        } else {
            "${parent.targetCollectionPath}.$targetPath"
        }
    }
}