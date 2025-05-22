package xyz.mahmoudahmed.dsl.collections

import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * Builder for defining mappings inside a collection iteration.
 */
class NestedMappingBuilder(
    private val parent: ForEachItemBuilder,
    private val targetCollection: String
) {
    /**
     * Maps a source field to a target field.
     *
     * @param sourcePath Path to the source field
     * @return Builder for configuring the nested mapping
     */
    fun map(sourcePath: String): NestedSourcePathBuilder {
        return NestedSourcePathBuilder(this, sourcePath)
    }

    /**
     * Finishes the nested mapping configuration.
     *
     * @return The parent forEach item builder
     */
    fun end(): ForEachItemBuilder {
        return parent
    }

    internal fun addNestedRule(rule: MappingRule) {
        parent.addNestedRule(rule)
    }

    /**
     * The full path to the target collection.
     */
    val targetCollectionPath: String
        get() = targetCollection
}