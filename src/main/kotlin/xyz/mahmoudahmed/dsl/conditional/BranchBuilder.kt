package xyz.mahmoudahmed.dsl.conditional

import xyz.mahmoudahmed.dsl.builders.TargetBuilder

/**
 * Builder for creating conditional branching logic in mappings.
 */
class BranchBuilder(
    private val parent: TargetBuilder
) {
    private val branches = mutableListOf<ConditionalBranch>()

    /**
     * Defines a condition for a branch.
     *
     * @param condition Predicate that determines if the branch should execute
     * @return Builder for configuring the condition
     */
    fun doWhen(condition: (Any) -> Boolean): BranchConditionBuilder {
        return BranchConditionBuilder(this, condition)
    }

    /**
     * Creates a default branch that always executes if no other branches match.
     *
     * @return Builder for configuring the default branch actions
     */
    fun otherwise(): BranchActionBuilder {
        return BranchActionBuilder(this) { true }
    }

    /**
     * Finishes the branch configuration.
     *
     * @return The parent target builder
     */
    fun end(): TargetBuilder {
        parent.addRule(BranchMapping(branches))
        return parent
    }

    internal fun addBranch(branch: ConditionalBranch) {
        branches.add(branch)
    }
}