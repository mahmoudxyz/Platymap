package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationResult

/**
 * Base class for validation rules.
 */
abstract class ValidationRule {
    /**
     * Validates data against this rule.
     */
    abstract fun validate(context: ValidationContext): ValidationResult
}