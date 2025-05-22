package xyz.mahmoudahmed.dsl.typed

class TypedNestedTargetBuilder<S, T, V, C, P, R>(
    private val parent: TypedForEachItemBuilder<S, T, V, C>,
    private val sourceAccessor: (V) -> P,
    private val targetSetter: (C, R) -> Unit,
    private val transformation: ((P) -> Any)?
) {
    fun end(): TypedForEachItemBuilder<S, T, V, C> {
        // Use the properly typed mapping rule
        val rule = TypedNestedSimpleMapping<V, P, C, R>(sourceAccessor, targetSetter, transformation)
        parent.addNestedRule(rule)
        return parent
    }
}