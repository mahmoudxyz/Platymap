package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.config.ValidationConfig
import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult

/**
 * Base class for all validators.
 */
abstract class DataValidator {
    val rules = mutableListOf<ValidationRule>()

    /**
     * Validates data against all rules.
     */
    fun validate(data: Any, config: ValidationConfig = ValidationConfig()): ValidationResult {
        // Create context and allow subclasses to initialize it
        val context = createContext(data, config)

        var combined = ValidationResult.valid()

        for (rule in rules) {
            val result = rule.validate(context)

            if (!result.isValid) {
                if (config.failFast) {
                    return result
                }

                // Filter errors based on severity
                val filteredErrors = result.errors.filter { error ->
                    when (error.severity) {
                        ValidationError.Severity.ERROR -> true
                        ValidationError.Severity.WARNING -> config.includeWarnings
                        ValidationError.Severity.INFO -> config.includeInfos
                    }
                }

                if (filteredErrors.isNotEmpty()) {
                    combined = combined.merge(ValidationResult.invalid(filteredErrors))
                }
            }
        }

        return combined
    }

    /**
     * Creates and initializes the validation context.
     * Subclasses can override this to customize context initialization.
     */
    protected open fun createContext(data: Any, config: ValidationConfig): ValidationContext {
        val context = ValidationContext(data)
        // Initialize with logging configuration
        context.properties["includeDataInLogs"] = config.includeDataInLogs
        return context
    }

    /**
     * Adds a validation rule.
     */
    fun addRule(rule: ValidationRule) {
        rules.add(rule)
    }
}