package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.core.MappingRule

class TypedForEachItemBuilder<S, T, V, C>(
    private val parent: TypedTargetBuilder<S, T>,
    private val collectionAccessor: (S) -> List<V>,
    private val targetCollectionSetter: (T, List<C>) -> Unit
) {
    private val nestedRules = mutableListOf<MappingRule>()

    fun <P> map(sourceAccessor: (V) -> P): TypedNestedMappingBuilder<S, T, V, C, P> {
        return TypedNestedMappingBuilder(this, sourceAccessor)
    }

    fun end(): TypedTargetBuilder<S, T> {
        parent.addRule(TypedForEachMapping(collectionAccessor, targetCollectionSetter, nestedRules))
        return parent
    }

    internal fun addNestedRule(rule: MappingRule) {
        nestedRules.add(rule)
    }
}