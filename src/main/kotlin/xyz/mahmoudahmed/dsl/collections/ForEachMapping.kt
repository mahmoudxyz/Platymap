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
    private val targetCollectionPath: String,
    private val nestedRules: List<MappingRule>
) : MappingRule {

    override fun apply(context: MappingContext, target: Any) {
        val collection = context.getValueByPath(collectionPath) ?: return

        // Create target collection as DataNode.ArrayNode instead of a regular ArrayList
        val targetArray = DataNode.ArrayNode()

        when (collection) {
            is DataNode.ArrayNode -> {
                for (item in collection.elements) {
                    // Create a new target object for this collection item
                    val targetItem = DataNode.ObjectNode()

                    // Set the current item as a variable for nested rules to reference
                    val previousValue = context.getVariable(itemName)
                    context.setVariable(itemName, item)

                    try {
                        // Apply all nested rules to populate the target item
                        for (rule in nestedRules) {
                            rule.apply(context, targetItem)
                        }

                        // Add the populated item to the target array
                        targetArray.add(targetItem)
                    } finally {
                        // Restore previous variable value (if any)
                        if (previousValue != null) {
                            context.setVariable(itemName, previousValue)
                        } else {
                            context.removeVariable(itemName)
                        }
                    }
                }
            }
            is Collection<*> -> {
                for (item in collection) {
                    if (item != null) {
                        // Create a new target object for this collection item
                        val targetItem = DataNode.ObjectNode()

                        // Set the current item as a variable for nested rules to reference
                        val previousValue = context.getVariable(itemName)
                        context.setVariable(itemName, item)

                        try {
                            // Apply all nested rules to populate the target item
                            for (rule in nestedRules) {
                                rule.apply(context, targetItem)
                            }

                            // Add the populated item to the target array
                            targetArray.add(targetItem)
                        } finally {
                            // Restore previous variable value (if any)
                            if (previousValue != null) {
                                context.setVariable(itemName, previousValue)
                            } else {
                                context.removeVariable(itemName)
                            }
                        }
                    }
                }
            }
            is Array<*> -> {
                for (item in collection) {
                    if (item != null) {
                        // Create a new target object for this collection item
                        val targetItem = DataNode.ObjectNode()

                        // Set the current item as a variable for nested rules to reference
                        val previousValue = context.getVariable(itemName)
                        context.setVariable(itemName, item)

                        try {
                            // Apply all nested rules to populate the target item
                            for (rule in nestedRules) {
                                rule.apply(context, targetItem)
                            }

                            // Add the populated item to the target array
                            targetArray.add(targetItem)
                        } finally {
                            // Restore previous variable value (if any)
                            if (previousValue != null) {
                                context.setVariable(itemName, previousValue)
                            } else {
                                context.removeVariable(itemName)
                            }
                        }
                    }
                }
            }
            else -> {
                Logger.getLogger("Platymap").warn("Unsupported collection type: ${collection::class.simpleName}")
                return
            }
        }

        // Set the populated array at the target collection path
        context.setValueByPath(targetCollectionPath, targetArray, target)
    }
}