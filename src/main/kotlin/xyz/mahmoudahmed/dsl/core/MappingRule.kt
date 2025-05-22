package xyz.mahmoudahmed.dsl.core

/**
 * Core interface for all mapping rules.
 */
interface MappingRule {
    /**
     * Applies this mapping rule to transform source data to target data.
     *
     * @param context The context containing source data and variables
     * @param target The target object to populate
     */
    fun apply(context: MappingContext, target: Any)
}
