package xyz.mahmoudahmed.dsl.structure

import xyz.mahmoudahmed.dsl.builders.TargetBuilder

/**
 * Builder for specifying the target of nesting operations.
 */
class NestingTargetBuilder(
    private val parent: TargetBuilder,
    private val sourcePattern: String,
    private val structureName: String,
    private val fieldNameExtractor: String?,
    private val valueTransformation: ((String, Any) -> Any)?,
    private val isCollection: Boolean,
    private val includeNullValues: Boolean,
    private val strictTypeHandling: Boolean
) {
    /**
     * Specify the target path for the nested structure.
     *
     * @param targetPath Path where the nested structure will be created
     * @return The parent target builder
     */
    fun to(targetPath: String): TargetBuilder {
        validateTargetPath(targetPath)
        val rule = NestingMappingRule(
            sourcePattern, targetPath, structureName,
            fieldNameExtractor, valueTransformation, isCollection,
            includeNullValues, strictTypeHandling
        )
        println()
        parent.addRule(rule)
        return parent
    }

    private fun validateTargetPath(path: String) {
        if (path.contains("*")) {
            throw IllegalArgumentException("Target path cannot contain wildcards: $path")
        }
    }
}