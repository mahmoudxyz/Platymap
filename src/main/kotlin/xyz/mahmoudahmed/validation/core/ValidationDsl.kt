package xyz.mahmoudahmed.validation.core

import xyz.mahmoudahmed.dsl.builders.TargetBuilder
import xyz.mahmoudahmed.validation.builders.ValidationDslBuilder
import xyz.mahmoudahmed.validation.config.ValidationConfig
import xyz.mahmoudahmed.validation.integration.ValidatedMapping
import xyz.mahmoudahmed.validation.rules.PostValidator
import xyz.mahmoudahmed.validation.rules.ValidationMappingRule

/**
 * Extension to PlatyMap for validation.
 */
object ValidationDsl {
    /**
     * Creates a pre-validation builder.
     */
    fun preValidate(init: ValidationDslBuilder.() -> Unit): PreValidator {
        val builder = ValidationDslBuilder()
        builder.init()
        return builder.buildPreValidator()
    }

    /**
     * Creates an in-validation builder.
     */
    fun inValidate(init: ValidationDslBuilder.() -> Unit): InValidator {
        val builder = ValidationDslBuilder()
        builder.init()
        return builder.buildInValidator()
    }

    /**
     * Creates a post-validation builder.
     */
    fun postValidate(init: ValidationDslBuilder.() -> Unit): PostValidator {
        val builder = ValidationDslBuilder()
        builder.init()
        return builder.buildPostValidator()
    }
}

/**
 * Extension function for MappingBuilder to add in-validation.
 */
fun TargetBuilder.validateDuring(init: ValidationDslBuilder.() -> Unit): xyz.mahmoudahmed.dsl.builders.TargetBuilder {
    val validator = ValidationDsl.inValidate(init)
    this.addRule(ValidationMappingRule(validator))
    return this
}

/**
 * Creates a validated mapping with pre and post validation.
 */
fun createValidatedMapping(
    mapping: xyz.mahmoudahmed.dsl.core.Mapping,
    preValidator: PreValidator? = null,
    postValidator: PostValidator? = null,
    config: ValidationConfig = ValidationConfig()
): ValidatedMapping {
    return ValidatedMapping(mapping, preValidator, postValidator, config)
}