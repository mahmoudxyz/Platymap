package xyz.mahmoudahmed.dsl.typed


class TypedBranchMappingBuilder<S, T, V>(
    private val parent: TypedBranchActionBuilder<S, T>,
    private val sourceAccessor: (S) -> V
) {
    private var transformation: ((V) -> Any)? = null

    fun <U> to(targetSetter: (T, U) -> Unit): TypedBranchTargetBuilder<S, T, V, U> {
        return TypedBranchTargetBuilder(parent, sourceAccessor, targetSetter, transformation)
    }

    fun transform(transformation: (V) -> Any): TypedBranchMappingBuilder<S, T, V> {
        this.transformation = transformation
        return this
    }
}