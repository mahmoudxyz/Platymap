package xyz.mahmoudahmed.logging

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.rules.ValidationRule

/**
 * A validation rule that logs messages during validation.
 */
class ValidationLogRule(
    private val message: String,
    private val level: LogLevel,
    private val dataPaths: List<String> = emptyList()
) : ValidationRule() {

    override fun validate(context: ValidationContext): ValidationResult {
        // Extract data if paths are specified
        val dataToLog = if (dataPaths.isNotEmpty()) {
            dataPaths.associateWith { path ->
                try {
                    context.getValueByPath(path)
                } catch (e: Exception) {
                    "Error accessing path: ${e.message}"
                }
            }
        } else {
            // If no specific paths, check if we should log the entire data
            val includeData = context.properties["includeDataInLogs"] as? Boolean ?: false
            if (includeData) context.data else null
        }

        // Log the message
        Logger.log(level, message, dataToLog)

        // Log rules don't affect validation results
        return ValidationResult.valid()
    }
}