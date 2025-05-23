package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates that a string has a maximum length.
 */
class MaxLengthValidator(private val maxLength: Int) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path) ?: return ValidationResult.valid()

        val strValue = value.toString()
        return if (strValue.length <= maxLength) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "String length must be at most $maxLength, but was ${strValue.length}",
                    value,
                    "MAX_LENGTH"
                )
            )
        }
    }
}