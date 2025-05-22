package xyz.mahmoudahmed.dsl.typed

class TypedMappingBuilder<S, T, V>(
    private val parent: TypedTargetBuilder<S, T>,
    private val sourceAccessor: (S) -> V
) {
    private var transformation: ((V) -> Any)? = null
    private var condition: ((S) -> Boolean)? = null

    fun <U> to(targetSetter: (T, U) -> Unit): TypedTargetPathBuilder<S, T, V, U> {
        return TypedTargetPathBuilder(parent, sourceAccessor, targetSetter, transformation, condition)
    }

    fun transform(transformation: (V) -> Any): TypedMappingBuilder<S, T, V> {
        this.transformation = transformation
        return this
    }

    fun chooseIf(condition: (S) -> Boolean): TypedMappingBuilder<S, T, V> {
        this.condition = condition
        return this
    }
}