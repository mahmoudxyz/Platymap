package xyz.mahmoudahmed.dsl.bulk

import xyz.mahmoudahmed.dsl.builders.TargetBuilder
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * Builder for specifying the target of bulk mappings.
 */
class BulkTargetPathBuilder(
    private val parent: TargetBuilder,
    private val sourcePattern: String,
    private val targetPath: String,
    private val config: BulkMappingConfig
) {
    /**
     * Finalize the bulk mapping rule.
     */
    fun end(): TargetBuilder {
        val rule: MappingRule = createMappingRule()
        return parent.addRule(rule)
    }

    private fun createMappingRule(): MappingRule = when {
        config.flatten -> FlattenMappingRule(
            sourcePattern = sourcePattern,
            targetPath = targetPath,
            exclusions = config.exclusions,
            inclusions = config.inclusions,
            transformation = config.transformation,
            prefix = config.flattenPrefix
        )
        else -> BulkMappingRule(
            sourcePattern = sourcePattern,
            targetPath = targetPath,
            exclusions = config.exclusions,
            inclusions = config.inclusions,
            transformation = config.transformation,
            preserveStructure = config.preserveStructure
        )
    }
}
