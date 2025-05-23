package xyz.mahmoudahmed.validation.builders

import xyz.mahmoudahmed.validation.rules.ForEachValidationRule

/**
 * Builder for validating items in a collection.
 */
class ForEachValidationBuilder(
    private val parent: ValidationDslBuilder,
    private val collectionPath: String
) {
    private val builder = ValidationDslBuilder()

    /**
     * Applies validation rules to each item in the collection.
     *
     * @param init Lambda to configure validation rules for each item
     * @return The parent validation builder
     */
    operator fun invoke(init: ValidationDslBuilder.() -> Unit): ValidationDslBuilder {
        builder.init()

        // Create a ForEachValidator that will apply the nested rules to each item
        val nestedValidator = builder.buildPreValidator() // Type doesn't matter here
        parent.addRule(ForEachValidationRule(collectionPath, nestedValidator.rules))

        return parent
    }

    /**
     * Validates a field of each item.
     */
    fun validate(itemPath: String): FieldValidationBuilder {
        return FieldValidationBuilder(builder, itemPath)
    }

    /**
     * Finishes the forEach validation without using lambda.
     */
    fun end(): ValidationDslBuilder {
        val nestedValidator = builder.buildPreValidator() // Type doesn't matter here
        parent.addRule(ForEachValidationRule(collectionPath, nestedValidator.rules))
        return parent
    }
}

