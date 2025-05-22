package xyz.mahmoudahmed.dsl.conditional

import xyz.mahmoudahmed.dsl.builders.TargetBuilder
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.core.SimpleMapping
import java.util.ArrayList

/**
 * Represents a conditional branch in a mapping with a condition and associated actions.
 */
class ConditionalBranch(
    val condition: (Any) -> Boolean,
    actions: List<MappingRule>
) {
    private val _actions = ArrayList(actions)

    /**
     * Gets the list of actions to execute when the condition is true.
     */
    val actions: List<MappingRule>
        get() = _actions.toList()

    /**
     * Evaluates the condition against the provided data.
     *
     * @param data The data to test against the condition
     * @return true if the condition is satisfied, false otherwise
     */
    fun evaluate(data: Any): Boolean {
        return condition(data)
    }

    /**
     * Executes all the actions in this branch using the provided context and target.
     *
     * @param context The mapping context containing source data and variables
     * @param target The target object to apply the mapping to
     */
    fun executeActions(context: MappingContext, target: Any) {
        for (rule in _actions) {
            rule.apply(context, target)
        }
    }
}

