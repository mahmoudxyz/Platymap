package xyz.mahmoudahmed.dsl.bulk

import xyz.mahmoudahmed.dsl.builders.TargetBuilder

/**
 * Builder for bulk mapping operations with improved type safety.
 */
class BulkMappingBuilder(
    private val parent: TargetBuilder,
    private val sourcePattern: String,
    private val config: BulkMappingConfig = BulkMappingConfig()
) {
    /**
     * Exclude specific paths from the bulk mapping.
     */
    fun excluding(vararg paths: String): BulkMappingBuilder {
        require(paths.isNotEmpty()) { "At least one exclusion path must be provided" }
        val newConfig = config.copy(exclusions = config.exclusions + paths.toSet())
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Include only specific paths from the bulk mapping.
     */
    fun including(vararg paths: String): BulkMappingBuilder {
        require(paths.isNotEmpty()) { "At least one inclusion path must be provided" }
        val newConfig = config.copy(inclusions = config.inclusions + paths.toSet())
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Apply a transformation to each match with path context.
     */
    fun <T : Any, R : Any> transformEach(transformation: (path: String, value: T) -> R): BulkMappingBuilder {
        @Suppress("UNCHECKED_CAST")
        val typedTransformation = transformation as (String, Any) -> Any
        val newConfig = config.copy(transformation = typedTransformation)
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Apply a simple value transformation to each match.
     */
    fun <T : Any, R : Any> transformValues(valueTransformation: (T) -> R): BulkMappingBuilder {
        @Suppress("UNCHECKED_CAST")
        val wrappedTransformation: (String, Any) -> Any = { _, value ->
            valueTransformation(value as T) as Any
        }
        val newConfig = config.copy(transformation = wrappedTransformation)
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Flatten the structure instead of preserving hierarchy.
     */
    fun flatten(): BulkMappingBuilder {
        val newConfig = config.copy(preserveStructure = false, flatten = true)
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Flatten with a prefix for field names.
     */
    fun flattenWithPrefix(prefix: String): BulkMappingBuilder {
        require(prefix.isNotBlank()) { "Flatten prefix cannot be blank" }
        val newConfig = config.copy(
            preserveStructure = false,
            flatten = true,
            flattenPrefix = prefix
        )
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Preserve the nested structure when mapping.
     */
    fun preserveStructure(): BulkMappingBuilder {
        val newConfig = config.copy(preserveStructure = true, flatten = false)
        return BulkMappingBuilder(parent, sourcePattern, newConfig)
    }

    /**
     * Specify the target path.
     */
    fun to(targetPath: String): BulkTargetPathBuilder {
        require(targetPath.isNotBlank()) { "Target path cannot be blank" }
        return BulkTargetPathBuilder(parent, sourcePattern, targetPath, config)
    }
}