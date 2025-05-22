package xyz.mahmoudahmed.dsl.bulk

import xyz.mahmoudahmed.dsl.builders.TargetBuilder

/**
 * Immutable configuration for bulk mapping operations.
 */
data class BulkMappingConfig(
    val exclusions: Set<String> = emptySet(),
    val inclusions: Set<String> = emptySet(),
    val transformation: ((String, Any) -> Any)? = null,
    val preserveStructure: Boolean = true,
    val flatten: Boolean = false,
    val flattenPrefix: String = ""
)