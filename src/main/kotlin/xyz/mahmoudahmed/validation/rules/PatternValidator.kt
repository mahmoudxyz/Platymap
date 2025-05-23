package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates that a value matches a regex pattern.
 */
class PatternValidator(private val pattern: Regex) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path) ?: return ValidationResult.valid()

        val strValue = value.toString()
        return if (pattern.matches(strValue)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "Value must match pattern ${pattern.pattern}, but was $strValue",
                    value,
                    "PATTERN"
                )
            )
        }
    }
}