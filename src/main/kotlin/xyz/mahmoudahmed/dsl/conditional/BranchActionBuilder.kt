package xyz.mahmoudahmed.dsl.conditional

import xyz.mahmoudahmed.dsl.core.MappingRule


/**
 * Builder for configuring actions in a conditional branch.
 */
class BranchActionBuilder(
    private val parent: BranchBuilder,
    private val condition: (Any) -> Boolean
) {
    private val actions = mutableListOf<MappingRule>()

    /**
     * Maps a source field to a target field within this branch.
     *
     * @param sourcePath Path to the source field
     * @return Builder for configuring the mapping
     */
    fun map(sourcePath: String): BranchMappingBuilder {
        return BranchMappingBuilder(this, sourcePath)
    }

    /**
     * Finishes the branch action configuration.
     *
     * @return The parent branch builder
     */
    fun endBranch(): BranchBuilder {
        parent.addBranch(ConditionalBranch(condition, actions))
        return parent
    }

    internal fun addAction(rule: MappingRule) {
        actions.add(rule)
    }
}
