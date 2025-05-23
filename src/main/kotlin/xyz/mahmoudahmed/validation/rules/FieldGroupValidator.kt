package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validator for a group of related fields.
 */
class FieldGroupValidator(
    private val fieldPaths: List<String>,
    private val condition: (List<Any?>) -> Boolean,
    private val message: String,
    private val code: String = "FIELD_GROUP"
) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        // Get all field values
        val values = fieldPaths.map { context.getValueByPath(it) }

        return if (condition(values)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    fieldPaths.joinToString(", "),
                    message,
                    values,
                    code
                )
            )
        }
    }
}