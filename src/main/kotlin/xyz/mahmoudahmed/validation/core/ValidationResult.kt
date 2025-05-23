package xyz.mahmoudahmed.validation.core

/**
 * Represents the result of a validation operation.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError> = emptyList()
) {
    companion object {
        fun valid(): ValidationResult = ValidationResult(true)

        fun invalid(errors: List<ValidationError>): ValidationResult =
            ValidationResult(false, errors)

        fun invalid(error: ValidationError): ValidationResult =
            invalid(listOf(error))
    }

    fun merge(other: ValidationResult): ValidationResult {
        if (isValid && other.isValid) return valid()

        val allErrors = errors + other.errors
        return invalid(allErrors)
    }
}