package xyz.mahmoudahmed.dsl.conditional

import xyz.mahmoudahmed.dsl.core.SimpleMapping

/**
 * Builder for finalizing a mapping within a conditional branch.
 */
class BranchTargetBuilder(
    private val parent: BranchActionBuilder,
    private val sourcePath: String,
    private val targetPath: String,
    private val transformation: ((Any) -> Any)?
) {
    /**
     * Finishes the mapping configuration.
     *
     * @return The parent branch action builder
     */
    fun end(): BranchActionBuilder {
        val rule = SimpleMapping(sourcePath, targetPath, transformation, null)
        parent.addAction(rule)
        return parent
    }
}