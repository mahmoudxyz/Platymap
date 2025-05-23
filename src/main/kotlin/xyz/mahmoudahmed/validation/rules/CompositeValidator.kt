package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.CompositeType
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validator that combines two other validators using AND/OR logic.
 */
class CompositeValidator(
    private val left: Validator,
    private val right: Validator,
    private val type: CompositeType
) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val leftResult = left.validate(context, path)

        when (type) {
            CompositeType.AND -> {
                // Both must be valid
                val rightResult = right.validate(context, path)
                return leftResult.merge(rightResult)
            }
            CompositeType.OR -> {
                // If left is valid, we're done
                if (leftResult.isValid) return ValidationResult.valid()

                // Otherwise, check right
                val rightResult = right.validate(context, path)
                return if (rightResult.isValid) {
                    ValidationResult.valid()
                } else {
                    // Both failed - combine errors
                    ValidationResult.invalid(leftResult.errors + rightResult.errors)
                }
            }
        }
    }
}
