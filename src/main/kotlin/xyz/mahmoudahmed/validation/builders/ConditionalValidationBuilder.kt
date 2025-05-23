package xyz.mahmoudahmed.validation.builders

import xyz.mahmoudahmed.validation.rules.PathValidationRule

/**
 * Builder for creating a conditional validation group.
 */
class ConditionalValidationBuilder(
    private val parent: ValidationDslBuilder,
    private val condition: (Any) -> Boolean
) {
    private val builder = ValidationDslBuilder()

    /**
     * Applies validation rules when the condition is met.
     *
     * @param init Lambda to configure conditional validation rules
     * @return The parent validation builder
     */
    operator fun invoke(init: ValidationDslBuilder.() -> Unit): ValidationDslBuilder {
        builder.init()

        // Get the rules from the nested builder
        val nestedValidator = builder.buildPreValidator() // Type doesn't matter here

        // Add each rule to the parent with the condition applied
        for (rule in nestedValidator.rules) {
            if (rule is PathValidationRule) {
                val combinedCondition: (Any) -> Boolean = { data ->
                    condition(data) && (rule.condition?.invoke(data) ?: true)
                }

                parent.addRule(PathValidationRule(rule.path, rule.validator, combinedCondition))
            } else {
                // For other rule types, we'd need specific handling
                parent.addRule(rule)
            }
        }

        return parent
    }

    /**
     * Validates a field when the condition is met.
     */
    fun validate(path: String): FieldValidationBuilder {
        return FieldValidationBuilder(builder, path)
    }

    /**
     * Validates each item in a collection when the condition is met.
     */
    fun forEach(collectionPath: String): ForEachValidationBuilder {
        return ForEachValidationBuilder(builder, collectionPath)
    }

    /**
     * Finishes the conditional validation.
     */
    fun end(): ValidationDslBuilder {
        // Implementation similar to invoke, kept for backward compatibility
        // Get the rules from the nested builder
        val nestedValidator = builder.buildPreValidator()

        // Add each rule to the parent with the condition applied
        for (rule in nestedValidator.rules) {
            if (rule is PathValidationRule) {
                val combinedCondition: (Any) -> Boolean = { data ->
                    condition(data) && (rule.condition?.invoke(data) ?: true)
                }

                parent.addRule(PathValidationRule(rule.path, rule.validator, combinedCondition))
            } else {
                parent.addRule(rule)
            }
        }

        return parent
    }
}

/**
 * Extension function for the ValidationDslBuilder to create a conditional group.
 * This accepts both the condition and the validation lambda.
 */
fun ValidationDslBuilder.`when`(
    condition: (Any) -> Boolean,
    init: ValidationDslBuilder.() -> Unit
): ValidationDslBuilder {
    val builder = ConditionalValidationBuilder(this, condition)
    return builder.invoke(init)
}