package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator


/**
 * Validates that a value is not null.
 */
class NotNullValidator : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path)

        return if (value != null) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(path, "Value must not be null", null, "NOT_NULL")
            )
        }
    }
}