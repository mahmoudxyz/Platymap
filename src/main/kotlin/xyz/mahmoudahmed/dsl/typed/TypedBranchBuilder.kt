package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.conditional.BranchMapping
import xyz.mahmoudahmed.dsl.conditional.ConditionalBranch

class TypedBranchBuilder<S, T>(
    private val parent: TypedTargetBuilder<S, T>
) {
    private val branches = mutableListOf<ConditionalBranch>()

    fun chooseIf(condition: (S) -> Boolean): TypedBranchConditionBuilder<S, T> {
        return TypedBranchConditionBuilder(this, condition)
    }

    fun otherwise(): TypedBranchActionBuilder<S, T> {
        return TypedBranchActionBuilder(this) { true }
    }

    fun end(): TypedTargetBuilder<S, T> {
        parent.addRule(BranchMapping(branches))
        return parent
    }

    internal fun addBranch(branch: ConditionalBranch) {
        branches.add(branch)
    }
}