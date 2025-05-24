package xyz.mahmoudahmed.validation.config

/**
 * Configuration for validation behavior.
 */
data class ValidationConfig(
    val failFast: Boolean = false,
    val throwOnError: Boolean = true,
    val includeWarnings: Boolean = true,
    val includeInfos: Boolean = false,
    val includeDataInLogs: Boolean = false  // Add this property
)