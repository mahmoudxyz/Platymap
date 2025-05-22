package xyz.mahmoudahmed.dsl.collections

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.core.SimpleMapping

/**
 * Mapping rule for nested mappings within collections.
 */
class NestedSimpleMapping<T: Any>(
    private val sourcePath: String,
    private val targetPath: String,
    private val transformation: ((T) -> Any)?,
    private val condition: ((Any) -> Boolean)?
) : MappingRule {

    @Suppress("UNCHECKED_CAST")
    override fun apply(context: MappingContext, target: Any) {
        if (condition != null && !condition.invoke(context.sourceData)) {
            return
        }

        val sourceValue = context.getValueByPath(sourcePath) ?: return

        val targetValue = transformation?.invoke(sourceValue as T) ?: sourceValue

        if (target is DataNode.ObjectNode) {
            SimpleMapping.setValueInDataNode(target, targetPath, targetValue)
        }
    }
}