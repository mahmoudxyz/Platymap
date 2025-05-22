package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * A properly typed version of SimpleMapping that preserves the generic parameters
 * for the target setter.
 */
class TypedBranchMapping<S, T, V, U>(
    private val sourceAccessor: (S) -> V,
    private val targetSetter: (T, U) -> Unit,
    private val transformation: ((V) -> Any)?,
    private val condition: ((S) -> Boolean)?
) : MappingRule {

    override fun apply(context: MappingContext, target: Any) {
        @Suppress("UNCHECKED_CAST")
        val source = context.sourceData as S
        if (condition != null && !condition.invoke(source)) {
            return
        }

        val sourceValue = sourceAccessor(source)
        if (sourceValue == null) {
            return
        }

        val transformedValue = if (transformation != null) {
            transformation.invoke(sourceValue)
        } else {
            sourceValue
        }

        // Type cast here - we know T is the type of our target
        @Suppress("UNCHECKED_CAST")
        targetSetter(target as T, transformedValue as U)
    }
}