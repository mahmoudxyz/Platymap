package xyz.mahmoudahmed.dsl.collections

import org.yaml.snakeyaml.internal.Logger
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

/**
 * Mapping rule that iterates over a collection and applies nested rules.
 */
class ForEachMapping(
    private val collectionPath: String,
    private val itemName: String,
    nestedRules: List<MappingRule>
) : MappingRule {
    private val nestedRules = ArrayList(nestedRules)

    override fun apply(context: MappingContext, target: Any) {
        val collection = context.getValueByPath(collectionPath) ?: return

        when (collection) {
            is DataNode.ArrayNode -> {
                for (item in collection.elements) {
                    context.setVariable(itemName, item)
                    applyNestedRules(context, target)
                }
            }
            is Collection<*> -> {
                for (item in collection) {
                    if (item != null) {
                        context.setVariable(itemName, item)
                        applyNestedRules(context, target)
                    }
                }
            }
            else -> {
                Logger.getLogger("Platymap").warn("unsupported collection type")
            }
        }
    }

    private fun applyNestedRules(context: MappingContext, target: Any) {
        for (rule in nestedRules) {
            rule.apply(context, target)
        }
    }
}
