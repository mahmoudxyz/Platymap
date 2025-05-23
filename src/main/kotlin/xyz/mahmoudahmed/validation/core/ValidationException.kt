package xyz.mahmoudahmed.validation.core

/**
 * Exception thrown when validation fails.
 */
class ValidationException(
    message: String,
    val errors: List<ValidationError>
) : RuntimeException("$message: ${errors.joinToString("; ") { it.message }}")