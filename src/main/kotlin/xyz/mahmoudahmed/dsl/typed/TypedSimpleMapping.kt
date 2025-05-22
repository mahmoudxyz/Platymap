package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

class TypedSimpleMapping<S, V>(
    private val sourceAccessor: (S) -> V,
    private val targetSetter: (Any, Any) -> Unit,
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

        val targetValue = if (transformation != null) {
            transformation.invoke(sourceValue)
        } else {
            sourceValue
        }

        targetSetter(target, targetValue)
    }
}