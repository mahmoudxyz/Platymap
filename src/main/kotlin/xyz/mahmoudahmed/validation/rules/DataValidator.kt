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
        val context = ValidationContext(data)
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
     * Adds a validation rule.
     */
    fun addRule(rule: ValidationRule) {
        rules.add(rule)
    }
}