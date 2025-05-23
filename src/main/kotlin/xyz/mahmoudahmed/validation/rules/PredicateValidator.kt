package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates using a custom predicate function.
 */
class PredicateValidator(
    private val predicate: (Any?) -> Boolean,
    private val message: String,
    private val code: String = "CUSTOM"
) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path)

        return if (predicate(value)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(
                ValidationError(path, message, value, code)
            )
        }
    }
}