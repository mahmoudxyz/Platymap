package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates that a value is one of a set of allowed values.
 */
class AllowedValuesValidator(private val allowedValues: Set<Any?>) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path)

        return if (allowedValues.contains(value)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "Value must be one of $allowedValues, but was $value",
                    value,
                    "ALLOWED_VALUES"
                )
            )
        }
    }
}