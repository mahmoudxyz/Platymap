package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.dsl.bulk.BulkMappingBuilder
import xyz.mahmoudahmed.dsl.collections.ForEachBuilder
import xyz.mahmoudahmed.dsl.conditional.BranchBuilder
import xyz.mahmoudahmed.dsl.core.Mapping
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.structure.NestingBuilder
import xyz.mahmoudahmed.format.FormatType

class TargetBuilder internal constructor(
    private val sourceName: String,
    private val sourceFormat: FormatType,
    private val targetName: String,
    private val targetFormat: FormatType = FormatType.JSON,
    private val rules: MutableList<MappingRule> = mutableListOf()
) {
    fun withFormat(format: FormatType): TargetBuilder =
        TargetBuilder(sourceName, sourceFormat, targetName, format, rules)

    fun map(sourcePath: String): MappingBuilder =
        MappingBuilder(this, sourcePath)

    /**
     * Map all fields that match a pattern.
     */
    fun mapAll(sourcePattern: String): BulkMappingBuilder =
        BulkMappingBuilder(this, sourcePattern)

    /**
     * Map all fields except those that match the excluded patterns.
     */
    fun mapAllExcept(vararg excludePatterns: String): BulkMappingBuilder =
        BulkMappingBuilder(this, "*").excluding(*excludePatterns)

    /**
     * Group fields that match a pattern into a nested structure.
     */
    fun nest(sourcePattern: String): NestingBuilder =
        NestingBuilder(this, sourcePattern)

    /**
     * Flatten a nested structure.
     */
    fun flatten(sourcePath: String): BulkMappingBuilder =
        BulkMappingBuilder(this, sourcePath).flatten()

    fun forEach(collectionPath: String): ForEachBuilder =
        ForEachBuilder(this, collectionPath)

    fun branch(): BranchBuilder = BranchBuilder(this)

    // Build method to create the final mapping
    fun build(): Mapping = Mapping(sourceName, sourceFormat, targetName, targetFormat, rules.toList())

    // Internal method to add a rule - now thread-safe
    internal fun addRule(rule: MappingRule): TargetBuilder {
        rules.add(rule)
        return this
    }
}
