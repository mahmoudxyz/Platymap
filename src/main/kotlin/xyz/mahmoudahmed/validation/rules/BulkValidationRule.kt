package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validation rule for bulk validation of multiple fields matching a pattern.
 */
class BulkValidationRule(
    private val pattern: String,
    private val validator: Validator
) : ValidationRule() {
    override fun validate(context: ValidationContext): ValidationResult {
        // This would need to find all paths matching the pattern
        // For simplicity, we'll assume a pattern like "customer.*" matches all direct properties
        // of customer
        val parts = pattern.split(".")
        val prefix = parts.dropLast(1).joinToString(".")
        val lastPart = parts.last()

        if (lastPart != "*") {
            return ValidationResult.valid() // Not a wildcard pattern
        }

        val parent = if (prefix.isEmpty()) context.data else context.getValueByPath(prefix)
        if (parent == null) {
            return ValidationResult.valid() // Parent path doesn't exist
        }

        // Get all properties of the parent
        val properties = when (parent) {
            is DataNode.ObjectNode -> parent.properties.keys
            is Map<*, *> -> (parent as Map<String, *>).keys
            else -> {
                // Try reflection for other object types
                try {
                    parent::class.java.declaredFields.map { it.name }
                } catch (e: Exception) {
                    return ValidationResult.valid() // Can't get properties
                }
            }
        }

        var result = ValidationResult.valid()

        // Validate each property
        for (prop in properties) {
            val path = if (prefix.isEmpty()) prop else "$prefix.$prop"
            val propResult = validator.validate(context, path)
            result = result.merge(propResult)
        }

        return result
    }
}