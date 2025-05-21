package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.dsl.MappingContext;
import xyz.mahmoudahmed.dsl.MappingRule;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a conditional branch in a mapping with a condition and associated actions.
 */
public class ConditionalBranch {
    private final Predicate<Object> condition;
    private final List<MappingRule> actions;

    /**
     * Creates a new conditional branch with the specified condition and actions.
     *
     * @param condition The predicate that determines if this branch should execute
     * @param actions The list of mapping rules to execute when the condition is true
     */
    ConditionalBranch(Predicate<Object> condition, List<MappingRule> actions) {
        this.condition = condition;
        this.actions = new ArrayList<>(actions);
    }

    /**
     * Gets the condition for this branch.
     *
     * @return The predicate that determines if this branch should execute
     */
    public Predicate<Object> getCondition() {
        return condition;
    }

    /**
     * Gets the list of actions to execute when the condition is true.
     *
     * @return An unmodifiable list of mapping rules
     */
    public List<MappingRule> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Evaluates the condition against the provided data.
     *
     * @param data The data to test against the condition
     * @return true if the condition is satisfied, false otherwise
     */
    public boolean evaluate(Object data) {
        return condition.test(data);
    }

    /**
     * Executes all the actions in this branch using the provided context and target.
     *
     * @param context The mapping context containing source data and variables
     * @param target The target object to apply the mapping to
     */
    public void executeActions(MappingContext context, Object target) {
        for (MappingRule rule : actions) {
            rule.apply(context, target);
        }
    }
}