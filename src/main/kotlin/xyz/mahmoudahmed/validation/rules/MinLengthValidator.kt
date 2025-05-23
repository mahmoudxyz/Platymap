package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates that a string has a minimum length.
 */
class MinLengthValidator(private val minLength: Int) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path)

        if (value == null) {
            return ValidationResult.valid() // Let NotNullValidator handle this
        }

        val strValue = value.toString()
        return if (strValue.length >= minLength) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "String length must be at least $minLength, but was ${strValue.length}",
                    value,
                    "MIN_LENGTH"
                )
            )
        }
    }
}