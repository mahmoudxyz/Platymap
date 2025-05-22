package xyz.mahmoudahmed.dsl.conditional

/**
 * Builder for configuring a condition in a branch.
 */
class BranchConditionBuilder(
    private val parent: BranchBuilder,
    private val condition: (Any) -> Boolean
) {
    /**
     * Specifies the actions to perform when the condition is true.
     *
     * @return Builder for configuring branch actions
     */
    fun then(): BranchActionBuilder {
        return BranchActionBuilder(parent, condition)
    }
}