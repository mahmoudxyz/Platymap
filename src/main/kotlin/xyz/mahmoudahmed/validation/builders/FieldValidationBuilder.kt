package xyz.mahmoudahmed.validation.builders

import xyz.mahmoudahmed.validation.context.ValidationContext
import xyz.mahmoudahmed.validation.core.ValidationError
import xyz.mahmoudahmed.validation.core.ValidationResult
import xyz.mahmoudahmed.validation.core.Validator
import xyz.mahmoudahmed.validation.rules.*

/**
 * Builder for field validation.
 */
class FieldValidationBuilder(
    private val parent: ValidationDslBuilder,
    private val path: String
) {
    private var validator: Validator? = null
    private var condition: ((Any) -> Boolean)? = null
    private var severity: ValidationError.Severity = ValidationError.Severity.ERROR

    /**
     * Specifies that the field is required (not null).
     */
    fun required(): FieldValidationBuilder {
        addValidator(NotNullValidator())
        return this
    }

    /**
     * Specifies a minimum length for string fields.
     */
    fun minLength(length: Int): FieldValidationBuilder {
        addValidator(MinLengthValidator(length))
        return this
    }

    /**
     * Specifies a maximum length for string fields.
     */
    fun maxLength(length: Int): FieldValidationBuilder {
        addValidator(MaxLengthValidator(length))
        return this
    }

    /**
     * Specifies a minimum value for numeric fields.
     */
    fun min(value: Number): FieldValidationBuilder {
        addValidator(MinValueValidator(value))
        return this
    }

    /**
     * Specifies a maximum value for numeric fields.
     */
    fun max(value: Number): FieldValidationBuilder {
        addValidator(MaxValueValidator(value))
        return this
    }

    /**
     * Specifies a regex pattern for string fields.
     */
    fun pattern(regex: Regex): FieldValidationBuilder {
        addValidator(PatternValidator(regex))
        return this
    }

    /**
     * Specifies that the field must be an email address.
     */
    fun email(): FieldValidationBuilder {
        addValidator(EmailValidator())
        return this
    }

    /**
     * Specifies that the field must be a valid date.
     */
    fun date(format: String = "yyyy-MM-dd"): FieldValidationBuilder {
        addValidator(DateValidator(format))
        return this
    }

    /**
     * Specifies allowed values for the field.
     */
    fun allowedValues(vararg values: Any?): FieldValidationBuilder {
        addValidator(AllowedValuesValidator(values.toSet()))
        return this
    }

    /**
     * Specifies a custom validation using a predicate.
     */
    fun custom(predicate: (Any?) -> Boolean, message: String, code: String = "CUSTOM"): FieldValidationBuilder {
        addValidator(PredicateValidator(predicate, message, code))
        return this
    }

    /**
     * Specifies a condition for when the validation should be applied.
     */
    fun `when`(condition: (Any) -> Boolean): FieldValidationBuilder {
        this.condition = condition
        return this
    }

    /**
     * Sets the severity level for validation errors from this field.
     */
    fun withSeverity(severity: ValidationError.Severity): FieldValidationBuilder {
        this.severity = severity
        return this
    }

    /**
     * Marks validation errors from this field as warnings.
     */
    fun asWarning(): FieldValidationBuilder {
        return withSeverity(ValidationError.Severity.WARNING)
    }

    /**
     * Marks validation errors from this field as info messages.
     */
    fun asInfo(): FieldValidationBuilder {
        return withSeverity(ValidationError.Severity.INFO)
    }

    /**
     * Finishes the field validation configuration.
     */
    fun end(): ValidationDslBuilder {
        val finalValidator = if (validator != null) {
            // Wrap the validator to apply the severity
            object : Validator {
                override fun validate(context: ValidationContext, path: String): ValidationResult {
                    val result = validator!!.validate(context, path)
                    if (!result.isValid) {
                        // Apply the severity to all errors
                        val errorsWithSeverity = result.errors.map {
                            it.copy(severity = severity)
                        }
                        return ValidationResult.invalid(errorsWithSeverity)
                    }
                    return result
                }
            }
        } else {
            NotNullValidator()
        }

        val rule = PathValidationRule(path, finalValidator, condition)
        parent.addRule(rule)
        return parent
    }

    private fun addValidator(newValidator: Validator) {
        validator = if (validator != null) {
            validator!! and newValidator
        } else {
            newValidator
        }
    }

    /**
     * Specifies that the field is required only when another field has a specific value.
     */
    fun FieldValidationBuilder.requiredIf(dependentPath: String, vararg dependentValues: Any?): FieldValidationBuilder {
        addValidator(RequiredIfValidator(dependentPath, dependentValues.toSet()))
        return this
    }



    /**
     * Validates the size of a collection.
     */
    fun size(min: Int? = null, max: Int? = null): FieldValidationBuilder {
        addValidator(CollectionSizeValidator(min, max))
        return this
    }

    /**
     * Adds field group validation to the ValidationDslBuilder.
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

}