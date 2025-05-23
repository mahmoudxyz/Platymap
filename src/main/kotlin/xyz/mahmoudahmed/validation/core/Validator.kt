package xyz.mahmoudahmed.validation.core

import xyz.mahmoudahmed.validation.rules.CompositeValidator
import xyz.mahmoudahmed.validation.context.ValidationContext

/**
 * Interface for all validators.
 */
interface Validator {
    /**
     * Validates the data.
     *
     * @param context The validation context containing data and variables
     * @param path The path to validate
     * @return The validation result
     */
    fun validate(context: ValidationContext, path: String): ValidationResult

    /**
     * Creates a new validator that combines this validator with another using AND logic.
     */
    infix fun and(other: Validator): Validator = CompositeValidator(this, other, CompositeType.AND)

    /**
     * Creates a new validator that combines this validator with another using OR logic.
     */
    infix fun or(other: Validator): Validator = CompositeValidator(this, other, CompositeType.OR)
}

/**
 * Types of composite validators.
 */
enum class CompositeType {
    AND, OR
}
