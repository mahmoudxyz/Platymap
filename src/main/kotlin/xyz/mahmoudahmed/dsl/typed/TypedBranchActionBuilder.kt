package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.conditional.ConditionalBranch
import xyz.mahmoudahmed.dsl.core.MappingRule

class TypedBranchActionBuilder<S, T>(
    private val parent: TypedBranchBuilder<S, T>,
    private val condition: (Any) -> Boolean
) {
    private val actions = mutableListOf<MappingRule>()

    fun <V> map(sourceAccessor: (S) -> V): TypedBranchMappingBuilder<S, T, V> {
        return TypedBranchMappingBuilder(this, sourceAccessor)
    }

    fun endBranch(): TypedBranchBuilder<S, T> {
        parent.addBranch(ConditionalBranch(condition, actions))
        return parent
    }

    internal fun addAction(rule: MappingRule) {
        actions.add(rule)
    }
}