package xyz.mahmoudahmed.validation.builders

import xyz.mahmoudahmed.logging.LogLevel
import xyz.mahmoudahmed.logging.ValidationLogRule
import xyz.mahmoudahmed.validation.core.InValidator
import xyz.mahmoudahmed.validation.core.PreValidator
import xyz.mahmoudahmed.validation.rules.FieldGroupValidator
import xyz.mahmoudahmed.validation.rules.PathValidationRule
import xyz.mahmoudahmed.validation.rules.PostValidator
import xyz.mahmoudahmed.validation.rules.ValidationRule

/**
 * Main builder for validation.
 */
class ValidationDslBuilder {
    private val rules = mutableListOf<ValidationRule>()

    /**
     * Validates a specific field.
     */
    fun validate(path: String): FieldValidationBuilder {
        return FieldValidationBuilder(this, path)
    }

    /**
     * Adds a validation rule.
     */
    internal fun addRule(rule: ValidationRule) {
        rules.add(rule)
    }

    /**
     * Creates a validation group with a condition.
     */
    fun `when`(condition: (Any) -> Boolean): ConditionalValidationBuilder {
        return ConditionalValidationBuilder(this, condition)
    }

    /**
     * Validates each item in a collection.
     */
    fun forEach(collectionPath: String, function: () -> ValidationDslBuilder): ForEachValidationBuilder {
        return ForEachValidationBuilder(this, collectionPath)
    }

    /**
     * Validates all fields matching a pattern.
     */
    fun validateAll(pattern: String): BulkValidationBuilder {
        return BulkValidationBuilder(this, pattern)
    }

    /**
     * Builds a pre-validator.
     */
    fun buildPreValidator(): PreValidator {
        val validator = PreValidator()
        rules.forEach { validator.addRule(it) }
        return validator
    }

    /**
     * Builds an in-validator.
     */
    fun buildInValidator(): InValidator {
        val validator = InValidator()
        rules.forEach { validator.addRule(it) }
        return validator
    }

    /**
     * Builds a post-validator.
     */
    fun buildPostValidator(): PostValidator {
        val validator = PostValidator()
        rules.forEach { validator.addRule(it) }
        return validator
    }

    /**
     * Validates that fields in a group satisfy a condition.
     */
    fun ValidationDslBuilder.validateGroup(
        vararg fieldPaths: String,
        message: String,
        code: String = "FIELD_GROUP",
        condition: (List<Any?>) -> Boolean
    ): ValidationDslBuilder {
        // Since FieldGroupValidator requires a path, we'll use the first field as the reference
        // but the error will include all fields
        val firstPath = fieldPaths.firstOrNull() ?: return this

        val validator = FieldGroupValidator(fieldPaths.toList(), condition, message, code)
        addRule(PathValidationRule(firstPath, validator))
        return this
    }

    /**
     * Adds a log message at INFO level during validation
     */
    fun log(message: String, level: LogLevel = LogLevel.INFO): ValidationDslBuilder {
        rules.run { add(ValidationLogRule(message, level, emptyList())) }
        return this
    }

    /**
     * Logs specific data paths during validation
     */
    fun logData(message: String, vararg paths: String, level: LogLevel = LogLevel.INFO): ValidationDslBuilder {
        rules.add(ValidationLogRule(message, level, paths.toList()))
        return this
    }
    fun trace(message: String): ValidationDslBuilder = log(message, LogLevel.TRACE)
    fun debug(message: String): ValidationDslBuilder = log(message, LogLevel.DEBUG)
    fun info(message: String): ValidationDslBuilder = log(message, LogLevel.INFO)
    fun warn(message: String): ValidationDslBuilder = log(message, LogLevel.WARN)
    fun error(message: String): ValidationDslBuilder = log(message, LogLevel.ERROR)

}