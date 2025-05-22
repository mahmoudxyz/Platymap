package xyz.mahmoudahmed.dsl.typed

class TypedTargetPathBuilder<S, T, V, U>(
    private val parent: TypedTargetBuilder<S, T>,
    private val sourceAccessor: (S) -> V,
    private val targetSetter: (T, U) -> Unit,
    private var transformation: ((V) -> Any)?,
    private var condition: ((S) -> Boolean)?
) {
    fun using(function: (V) -> U): TypedTargetBuilder<S, T> {
        @Suppress("UNCHECKED_CAST")
        this.transformation = function as (V) -> Any
        finalizeMappingRule()
        return parent
    }

    fun chooseIf(condition: (S) -> Boolean): TypedTargetBuilder<S, T> {
        this.condition = condition
        finalizeMappingRule()
        return parent
    }

    fun end(): TypedTargetBuilder<S, T> {
        finalizeMappingRule()
        return parent
    }

    private fun finalizeMappingRule() {
        val rule = TypedPropertyMapping(sourceAccessor, targetSetter, transformation, condition)
        parent.addRule(rule)
    }
}