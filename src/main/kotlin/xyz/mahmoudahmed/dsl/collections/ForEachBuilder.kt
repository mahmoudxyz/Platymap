package xyz.mahmoudahmed.dsl.collections

import xyz.mahmoudahmed.dsl.builders.TargetBuilder

/**
 * Builder for iterating over a collection during mapping.
 */
class ForEachBuilder(
    private val parent: TargetBuilder,
    private val collectionPath: String
) {
    /**
     * Defines the name to use for each item in the collection.
     *
     * @param itemName The variable name to use for each item
     * @return Builder for configuring nested mappings
     */
    fun `as`(itemName: String): ForEachItemBuilder {
        return ForEachItemBuilder(parent, collectionPath, itemName)
    }
}