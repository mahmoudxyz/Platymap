package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule


/**
 * MappingRule implementation for typed nested mappings that matches the generic parameters
 * used in TypedNestedTargetBuilder.
 */
class TypedNestedSimpleMapping<V, P, C, R>(
    private val sourceAccessor: (V) -> P,
    private val targetSetter: (C, R) -> Unit,
    private val transformation: ((P) -> Any)?
) : MappingRule {

    @Suppress("UNCHECKED_CAST")
    override fun apply(context: MappingContext, target: Any) {
        val source = context.sourceData as V
        val typedTarget = target as C

        val sourceValue = sourceAccessor(source) ?: return

        val transformedValue = if (transformation != null) {
            transformation.invoke(sourceValue)
        } else {
            sourceValue
        }

        // Safe cast - we know the target setter expects type R
        targetSetter(typedTarget, transformedValue as R)
    }
}