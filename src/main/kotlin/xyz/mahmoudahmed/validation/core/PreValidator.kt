package xyz.mahmoudahmed.validation.core

import xyz.mahmoudahmed.validation.config.ValidationConfig
import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.rules.DataValidator

/**
 * Validator for pre-mapping validation (source data).
 */
class PreValidator : DataValidator() {
    /**
     * Custom initialization for pre-validation context.
     */
    override fun createContext(data: Any, config: ValidationConfig): ValidationContext {
        val context = super.createContext(data, config)
        context.properties["validatorType"] = "pre-validation"
        return context
    }
}