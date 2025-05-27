package xyz.mahmoudahmed.dsl.collections

import xyz.mahmoudahmed.dsl.builders.TargetBuilder
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * Builder for configuring nested mappings within a collection iteration.
 */
class ForEachItemBuilder(
    private val parent: TargetBuilder,
    private val collectionPath: String,
    private val itemName: String
) {
    private val nestedRules = mutableListOf<MappingRule>()
    private var targetCollection: String? = null

    /**
     * Creates a nested collection at the specified path.
     *
     * @param targetCollection Path where the collection will be created
     * @return Builder for nested mappings
     */
    fun create(targetCollection: String): NestedMappingBuilder {
        this.targetCollection = targetCollection
        return NestedMappingBuilder(this, targetCollection)
    }

    /**
     * Finishes the forEach configuration.
     *
     * @return The parent target builder
     */
    fun end(): TargetBuilder {
        if (targetCollection == null) {
            throw IllegalStateException("Target collection path not specified. Call create() before end().")
        }

        parent.addRule(ForEachMapping(collectionPath, itemName, targetCollection!!, nestedRules))
        return parent
    }

    /**
     * Adds a rule to the nested rules collection.
     *
     * @param rule The mapping rule to add
     */
    internal fun addNestedRule(rule: MappingRule) {
        nestedRules.add(rule)
    }
}