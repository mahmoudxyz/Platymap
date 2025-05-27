import xyz.mahmoudahmed.dsl.collections.NestedMappingBuilder
import xyz.mahmoudahmed.dsl.collections.NestedSimpleMapping

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
        // Just use the targetPath directly, no need to prefix it
        val rule = NestedSimpleMapping<Any>(
            sourcePath,
            targetPath,  // Use targetPath directly
            transformation,
            null
        )
        parent.addNestedRule(rule)
        return parent
    }

    private fun buildTargetPath(): String {
        return targetPath
    }
}