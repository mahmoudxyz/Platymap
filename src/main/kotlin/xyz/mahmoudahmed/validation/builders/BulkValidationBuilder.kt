package xyz.mahmoudahmed.validation.builders

import xyz.mahmoudahmed.validation.core.Validator
import xyz.mahmoudahmed.validation.rules.*

/**
 * Builder for bulk validation of multiple fields.
 */
class BulkValidationBuilder(
    private val parent: ValidationDslBuilder,
    private val pattern: String
) {
    private var validator: Validator? = null

    /**
     * Specifies that all matching fields are required.
     */
    fun required(): BulkValidationBuilder {
        validator = NotNullValidator()
        return this
    }

    /**
     * Specifies a minimum length for all matching string fields.
     */
    fun minLength(length: Int): BulkValidationBuilder {
        addValidator(MinLengthValidator(length))
        return this
    }

    /**
     * Specifies a maximum length for all matching string fields.
     */
    fun maxLength(length: Int): BulkValidationBuilder {
        addValidator(MaxLengthValidator(length))
        return this
    }

    /**
     * Specifies a custom validation for all matching fields.
     */
    fun custom(predicate: (Any?) -> Boolean, message: String): BulkValidationBuilder {
        addValidator(PredicateValidator(predicate, message))
        return this
    }

    /**
     * Finishes the bulk validation configuration.
     */
    fun end(): ValidationDslBuilder {
        // Create a BulkValidator that will find all matching fields and apply the validator
        parent.addRule(BulkValidationRule(pattern, validator ?: NotNullValidator()))
        return parent
    }

    private fun addValidator(newValidator: Validator) {
        validator = if (validator != null) {
            validator!! and newValidator
        } else {
            newValidator
        }
    }
}
