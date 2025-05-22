package xyz.mahmoudahmed.dsl.conditional

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule
import java.util.ArrayList

/**
 * Implements conditional branching in the mapping DSL.
 * Allows for different mapping rules to be applied based on conditions.
 */
class BranchMapping(
    branches: List<ConditionalBranch>
) : MappingRule {
    private val branches = ArrayList(branches)

    override fun apply(context: MappingContext, target: Any) {
        for (branch in branches) {
            if (branch.condition(context.sourceData)) {
                for (rule in branch.actions) {
                    rule.apply(context, target)
                }
                break
            }
        }
    }
}
