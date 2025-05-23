package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator


/**
 * Validates that a number is greater than or equal to a minimum value.
 */
class MinValueValidator(private val minValue: Number) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path) ?: return ValidationResult.valid()

        if (value !is Number) {
            return ValidationResult.invalid(
                ValidationError(
                    path,
                    "Expected a number, but got ${value::class.simpleName}",
                    value,
                    "TYPE_MISMATCH"
                )
            )
        }

        val doubleValue = value.toDouble()
        val doubleMin = minValue.toDouble()

        return if (doubleValue >= doubleMin) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "Value must be at least $minValue, but was $value",
                    value,
                    "MIN_VALUE"
                )
            )
        }
    }
}