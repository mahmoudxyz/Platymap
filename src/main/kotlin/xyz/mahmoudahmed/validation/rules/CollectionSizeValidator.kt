package xyz.mahmoudahmed.validation.rules

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator

/**
 * Validates the size of a collection.
 */
class CollectionSizeValidator(
    private val minSize: Int? = null,
    private val maxSize: Int? = null
) : Validator {
    override fun validate(context: ValidationContext, path: String): ValidationResult {
        val value = context.getValueByPath(path) ?: return ValidationResult.valid()

        val size = when (value) {
            is Collection<*> -> value.size
            is Array<*> -> value.size
            is Map<*, *> -> value.size
            is DataNode.ArrayNode -> value.elements.size
            is DataNode.ObjectNode -> value.properties.size
            is String -> value.length
            else -> return ValidationResult.invalid(
                ValidationError(
                    path,
                    "Expected a collection, array, map, or string, but got ${value::class.simpleName}",
                    value,
                    "TYPE_MISMATCH"
                )
            )
        }

        if (minSize != null && size < minSize) {
            return ValidationResult.invalid(
                ValidationError(
                    path,
                    "Size must be at least $minSize, but was $size",
                    value,
                    "MIN_SIZE"
                )
            )
        }

        if (maxSize != null && size > maxSize) {
            return ValidationResult.invalid(
                ValidationError(
                    path,
                    "Size must be at most $maxSize, but was $size",
                    value,
                    "MAX_SIZE"
                )
            )
        }

        return ValidationResult.valid()
    }
}