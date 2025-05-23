package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates dates against a format.
 */
class DateValidator(private val format: String = "yyyy-MM-dd") : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path) ?: return ValidationResult.valid()

        val strValue = value.toString()
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern(format)
            java.time.LocalDate.parse(strValue, formatter)
            ValidationResult.valid()
        } catch (e: Exception) {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "Invalid date format: $strValue. Expected format: $format",
                    value,
                    "DATE_FORMAT"
                )
            )
        }
    }
}