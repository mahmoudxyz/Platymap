package xyz.mahmoudahmed.dsl.structure

import xyz.mahmoudahmed.dsl.builders.TargetBuilder

/**
 * Builder for nesting operations that restructure flat data into hierarchical structures.
 */
class NestingBuilder(
    private val parent: TargetBuilder,
    private val sourcePattern: String
) {
    private var fieldNameExtractor: String? = null
    private var valueTransformation: ((String, Any) -> Any)? = null
    private var includeNullValues: Boolean = false
    private var strictTypeHandling: Boolean = false

    /**
     * Specify the field to use for extracting the property name.
     *
     * @param fieldNameExtractor The name of the field to store the original property name
     * @return This builder for chaining
     */
    fun withFieldNameFrom(fieldNameExtractor: String): NestingBuilder {
        this.fieldNameExtractor = fieldNameExtractor
        return this
    }

    /**
     * Apply a transformation to the values.
     *
     * @param valueTransformation The transformation function to apply to each value
     * @return This builder for chaining
     */
    fun transformValues(valueTransformation: (String, Any) -> Any): NestingBuilder {
        this.valueTransformation = valueTransformation
        return this
    }

    /**
     * Include null values in the nested structure.
     * By default, null values are skipped.
     *
     * @return This builder for chaining
     */
    fun includeNullValues(): NestingBuilder {
        this.includeNullValues = true
        return this
    }

    /**
     * Enable strict type handling for transformations.
     * When enabled, type mismatches will throw exceptions instead of trying to convert.
     *
     * @return This builder for chaining
     */
    fun withStrictTypeHandling(): NestingBuilder {
        this.strictTypeHandling = true
        return this
    }

    /**
     * Apply a transformation to keys before they are used in the nested structure.
     *
     * @param keyTransformation The transformation function to apply to each key
     * @return This builder for chaining
     */
    fun transformKeys(keyTransformation: (String) -> String): NestingBuilder {
        val existingTransformation = this.valueTransformation
        this.valueTransformation = { key, value ->
            val transformedKey = keyTransformation(key)
            if (existingTransformation != null) {
                existingTransformation(transformedKey, value)
            } else {
                value
            }
        }
        return this
    }

    /**
     * Specify the target path and create a collection from matching fields.
     *
     * @param collectionName The name to give the generated collection
     * @return Builder for finalizing the nesting operation
     */
    fun asCollection(collectionName: String): NestingTargetBuilder {
        validateCollectionName(collectionName)
        return NestingTargetBuilder(
            parent, sourcePattern, collectionName,
            fieldNameExtractor, valueTransformation, true,
            includeNullValues, strictTypeHandling
        )
    }

    /**
     * Specify the target path and create an object from matching fields.
     *
     * @param objectName The name to give the generated object
     * @return Builder for finalizing the nesting operation
     */
    fun asObject(objectName: String): NestingTargetBuilder {
        validateObjectName(objectName)
        return NestingTargetBuilder(
            parent, sourcePattern, objectName,
            fieldNameExtractor, valueTransformation, false,
            includeNullValues, strictTypeHandling
        )
    }

    private fun validateCollectionName(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Collection name cannot be empty")
        }
        if (name.contains(".")) {
            throw IllegalArgumentException("Collection name cannot contain dots: $name")
        }
    }

    private fun validateObjectName(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Object name cannot be empty")
        }
        if (name.contains(".")) {
            throw IllegalArgumentException("Object name cannot contain dots: $name")
        }
    }
}
