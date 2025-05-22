package xyz.mahmoudahmed.dsl.typed

class TypedNestedMappingBuilder<S, T, V, C, P>(
    private val parent: TypedForEachItemBuilder<S, T, V, C>,
    private val sourceAccessor: (V) -> P
) {
    private var transformation: ((P) -> Any)? = null

    fun <R> to(targetSetter: (C, R) -> Unit): TypedNestedTargetBuilder<S, T, V, C, P, R> {
        return TypedNestedTargetBuilder(parent, sourceAccessor, targetSetter, transformation)
    }

    fun transform(transformation: (P) -> Any): TypedNestedMappingBuilder<S, T, V, C, P> {
        this.transformation = transformation
        return this
    }
}