package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * A validation rule for a specific path.
 */
class PathValidationRule(
    val path: String,
    val validator: Validator,
    val condition: ((Any) -> Boolean)? = null
) : ValidationRule() {
    override fun validate(context: ValidationContext): ValidationResult {
        // Skip validation if condition is not met
        if (condition != null && !condition.invoke(context.data)) {
            return ValidationResult.valid()
        }

        return validator.validate(context, path)
    }
}