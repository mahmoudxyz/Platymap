package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult

/**
 * Validation rule for each item in a collection.
 */
class ForEachValidationRule(
    private val collectionPath: String,
    private val itemRules: List<ValidationRule>
) : ValidationRule() {
    override fun validate(context: ValidationContext): ValidationResult {
        val collection = context.getValueByPath(collectionPath)
        if (collection == null) {
            return ValidationResult.valid()
        }

        val items = when (collection) {
            is DataNode.ArrayNode -> collection.elements
            is Collection<*> -> collection.toList()
            is Array<*> -> collection.toList()
            else -> return ValidationResult.invalid(
                ValidationError(
                    collectionPath,
                    "Expected a collection at $collectionPath, but found ${collection::class.simpleName}",
                    collection,
                    "TYPE_MISMATCH"
                )
            )
        }

        if (items.isEmpty()) {
            return ValidationResult.valid()
        }

        var result = ValidationResult.valid()

        // Validate each item
        for (i in items.indices) {
            val itemContext = ValidationContext(items[i] ?: continue)

            for (rule in itemRules) {
                val ruleResult = when (rule) {
                    is PathValidationRule -> {
                        // For path rules, we need to adjust the paths in error messages
                        val itemResult = rule.validate(itemContext)
                        if (!itemResult.isValid) {
                            // Update error paths to include the collection path and index
                            val updatedErrors = itemResult.errors.map { error ->
                                error.copy(path = "$collectionPath[$i].${error.path}")
                            }
                            ValidationResult.invalid(updatedErrors)
                        } else {
                            ValidationResult.valid()
                        }
                    }
                    else -> rule.validate(itemContext)
                }

                result = result.merge(ruleResult)
            }
        }

        return result
    }
}
