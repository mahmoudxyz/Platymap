package xyz.mahmoudahmed.dsl.typed

class TypedBranchConditionBuilder<S, T>(
    private val parent: TypedBranchBuilder<S, T>,
    private val condition: (S) -> Boolean
) {
    fun then(): TypedBranchActionBuilder<S, T> {
        return TypedBranchActionBuilder(parent) { obj ->
            @Suppress("UNCHECKED_CAST")
            condition(obj as S)
        }
    }
}