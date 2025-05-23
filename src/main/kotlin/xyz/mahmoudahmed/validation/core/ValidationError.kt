package xyz.mahmoudahmed.validation.core

/**
 * Detailed information about a validation error.
 */
data class ValidationError(
    val path: String,
    val message: String,
    val value: Any? = null,
    val code: String = "",
    val severity: Severity = Severity.ERROR
) {
    enum class Severity {
        INFO, WARNING, ERROR
    }
}
