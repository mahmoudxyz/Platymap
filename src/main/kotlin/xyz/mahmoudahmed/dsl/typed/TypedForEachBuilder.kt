package xyz.mahmoudahmed.dsl.typed

class TypedForEachBuilder<S, T, V>(
    private val parent: TypedTargetBuilder<S, T>,
    private val collectionAccessor: (S) -> List<V>
) {
    fun <C> createIn(
        targetCollectionSetter: (T, List<C>) -> Unit
    ): TypedForEachItemBuilder<S, T, V, C> {
        return TypedForEachItemBuilder(parent, collectionAccessor, targetCollectionSetter)
    }
}