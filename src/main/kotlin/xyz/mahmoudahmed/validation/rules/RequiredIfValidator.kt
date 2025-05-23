package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates that a field is present only when another field has a specific value.
 */
class RequiredIfValidator(
    private val dependentPath: String,
    private val dependentValues: Set<Any?>
) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val dependentValue = context.getValueByPath(dependentPath)

        // If the dependent field doesn't have one of the expected values, skip validation
        if (!dependentValues.contains(dependentValue)) {
            return ValidationResult.valid()
        }

        // Check if the target field exists and is not null
        val value = context.getValueByPath(path)
        return if (value != null) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(
                    path,
                    "Field is required when $dependentPath is one of $dependentValues",
                    null,
                    "REQUIRED_IF"
                )
            )
        }
    }
}