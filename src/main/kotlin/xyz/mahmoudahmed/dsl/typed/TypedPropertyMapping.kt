package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * A fully typed mapping rule that preserves all generic parameters.
 */
class TypedPropertyMapping<S, T, V, U>(
    private val sourceAccessor: (S) -> V,
    private val targetSetter: (T, U) -> Unit,
    private val transformation: ((V) -> Any)?,
    private val condition: ((S) -> Boolean)?
) : MappingRule {

    @Suppress("UNCHECKED_CAST")
    override fun apply(context: MappingContext, target: Any) {
        val source = context.sourceData as S
        if (condition != null && !condition.invoke(source)) {
            return
        }

        val sourceValue = sourceAccessor(source) ?: return

        val transformedValue = if (transformation != null) {
            transformation.invoke(sourceValue)
        } else {
            sourceValue
        }

        // Safe cast with the generic type constraint
        targetSetter(target as T, transformedValue as U)
    }
}
