package xyz.mahmoudahmed.dsl.typed

class TypedBranchTargetBuilder<S, T, V, U>(
    private val parent: TypedBranchActionBuilder<S, T>,
    private val sourceAccessor: (S) -> V,
    private val targetSetter: (T, U) -> Unit,
    private val transformation: ((V) -> Any)?
) {
    fun end(): TypedBranchActionBuilder<S, T> {
        parent.addAction(TypedBranchMapping(sourceAccessor, targetSetter, transformation, null))
        return parent
    }
}