package xyz.mahmoudahmed.validation.integration

import xyz.mahmoudahmed.format.Format
import xyz.mahmoudahmed.validation.core.PreValidator
import xyz.mahmoudahmed.validation.core.ValidationDsl
import xyz.mahmoudahmed.validation.builders.ValidationDslBuilder
import xyz.mahmoudahmed.validation.config.ValidationConfig
import xyz.mahmoudahmed.validation.core.ValidationException
import xyz.mahmoudahmed.validation.rules.PostValidator

/**
 * Enhanced mapping that includes validation.
 */
class ValidatedMapping(
    private val mapping: xyz.mahmoudahmed.dsl.core.Mapping,
    private val preValidator: PreValidator? = null,
    private val postValidator: PostValidator? = null,
    private val validationConfig: ValidationConfig = ValidationConfig()
) {
    /**
     * Executes the mapping with validation.
     */
    fun execute(sourceData: Any): Any {
        // Pre-validation
        preValidator?.let {
            val preResult = it.validate(sourceData, validationConfig)
            if (!preResult.isValid && validationConfig.throwOnError) {
                throw ValidationException("Pre-validation failed", preResult.errors)
            }
        }

        // Execute mapping
        val result = mapping.execute(sourceData)

        // Post-validation
        postValidator?.let {
            val postResult = it.validate(result, validationConfig)
            if (!postResult.isValid && validationConfig.throwOnError) {
                throw ValidationException("Post-validation failed", postResult.errors)
            }
        }

        return result
    }
    /**
     * Executes the mapping and returns the result in the specified format.
     */
    fun executeToFormat(sourceData: Any, outputFormat: Format): String {
        val result = execute(sourceData)
        return mapping.executeToFormat(result, outputFormat)
    }

    /**
     * Executes the mapping to JSON format with validation.
     */
    fun executeToJson(sourceData: Any): String {
        return executeToFormat(sourceData, Format.JSON)
    }

    /**
     * Executes the mapping to XML format with validation.
     */
    fun executeToXml(sourceData: Any): String {
        return executeToFormat(sourceData, Format.XML)
    }
}

/**
 * Extension function to add pre-validation to a mapping.
 */
fun xyz.mahmoudahmed.dsl.builders.TargetBuilder.withPreValidation(
    init: ValidationDslBuilder.() -> Unit
): xyz.mahmoudahmed.dsl.builders.TargetBuilder {
    // Store the pre-validator in a property that will be accessed later
    this.properties["preValidator"] = ValidationDsl.preValidate(init)
    return this
}

/**
 * Extension function to add post-validation to a mapping.
 */
fun xyz.mahmoudahmed.dsl.builders.TargetBuilder.withPostValidation(
    init: ValidationDslBuilder.() -> Unit
): xyz.mahmoudahmed.dsl.builders.TargetBuilder {
    // Store the post-validator in a property that will be accessed later
    this.properties["postValidator"] = ValidationDsl.postValidate(init)
    return this
}

/**
 * Extension function to build a mapping with validation.
 */
fun xyz.mahmoudahmed.dsl.builders.TargetBuilder.buildWithValidation(
    config: ValidationConfig = ValidationConfig()
): ValidatedMapping {
    // Build the regular mapping
    val mapping = this.build()

    // Retrieve the validators from properties
    val preValidator = this.properties["preValidator"] as? PreValidator
    val postValidator = this.properties["postValidator"] as? PostValidator

    // Create and return the validated mapping
    return ValidatedMapping(mapping, preValidator, postValidator, config)
}

/**
 * Extension property to store arbitrary properties in TargetBuilder.
 * This would need to be added to the TargetBuilder class.
 */
val xyz.mahmoudahmed.dsl.builders.TargetBuilder.properties: MutableMap<String, Any>
    get() {
        // This is a simplified version. In a real implementation,
        // we would need to add a private field to TargetBuilder.
        return mutableMapOf<String, Any>()
    }