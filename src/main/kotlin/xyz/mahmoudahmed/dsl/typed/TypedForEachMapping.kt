package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

class TypedForEachMapping<S, T, V, C>(
    private val collectionAccessor: (S) -> List<V>,
    private val targetCollectionSetter: (T, List<C>) -> Unit,
    nestedRules: List<MappingRule>
) : MappingRule {
    private val nestedRules = ArrayList(nestedRules)

    @Suppress("UNCHECKED_CAST")
    override fun apply(context: MappingContext, target: Any) {
        val source = context.sourceData as S
        val sourceCollection = collectionAccessor(source)

        if (sourceCollection.isNullOrEmpty()) {
            return
        }

        val targetCollection = mutableListOf<C>()

        for (item in sourceCollection) {
            val targetItem = createTargetItem() as C

            val itemContext = MappingContext(item as Any)
            // Copy variables from parent context
            for ((key, value) in context.getVariables()) {
                itemContext.setVariable(key, value)
            }

            for (rule in nestedRules) {
                rule.apply(itemContext, targetItem as Any)
            }

            targetCollection.add(targetItem)
        }

        targetCollectionSetter(target as T, targetCollection)
    }

    private fun createTargetItem(): Any {
        return DataNode.ObjectNode()
    }
}