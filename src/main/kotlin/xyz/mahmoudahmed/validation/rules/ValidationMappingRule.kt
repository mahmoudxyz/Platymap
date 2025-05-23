package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingExecutionException
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.validation.core.InValidator

/**
 * A mapping rule that performs validation during mapping.
 */
class ValidationMappingRule(private val validator: InValidator) : MappingRule {
    override fun apply(context: MappingContext, target: Any) {
        val result = validator.validate(context.sourceData)
        if (!result.isValid) {
            throw MappingExecutionException(
                "Validation failed during mapping: ${result.errors.joinToString(", ") { it.message }}"
            )
        }
    }
}