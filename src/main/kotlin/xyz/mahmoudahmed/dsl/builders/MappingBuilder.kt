package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.dsl.transformers.MultiplyBy
import xyz.mahmoudahmed.dsl.transformers.TransformerRegistry

class MappingBuilder(
    private val parent: TargetBuilder,
    private val sourcePaths: List<String>
) {
    private var transformation: ((Any) -> Any)? = null
    private var multiFieldTransformation: ((List<Any?>) -> Any)? = null
    private var condition: ((Any) -> Boolean)? = null

    init {
        // Initialize transformer registry if needed
        TransformerRegistry.initialize()
    }


    /**
     * Overloaded to() method that returns appropriate builder type based on field count.
     * For single fields, returns TargetPathBuilder.
     * For multiple fields, returns MultiFieldTargetPathBuilder.
     */
    fun toM(targetPath: String): MultiFieldTargetPathBuilder {
        return MultiFieldTargetPathBuilder(
            parent,
            sourcePaths,
            targetPath,
            multiFieldTransformation,
            condition
        )

    }

    /**
     * Overloaded to() method that returns appropriate builder type based on field count.
     * For single fields, returns TargetPathBuilder.
     * For multiple fields, returns MultiFieldTargetPathBuilder.
     */
    fun to(targetPath: String): SingleFieldTargetPathBuilderImpl {

        return SingleFieldTargetPathBuilderImpl(
            parent,
            sourcePaths[0],
            targetPath,
            transformation,
            condition
        )
    }

    /**
     * Apply a named transformer from the registry.
     */
    fun applyTransformer(name: String): MappingBuilder {
        val transformer = TransformerRegistry.get(name)
            ?: throw IllegalArgumentException("Unknown transformer: $name")
        return transform(transformer::transform)
    }

    /**
     * Apply a transformation to the source value.
     * Transformations are composed so that multiple transformations can be applied.
     */
    fun transform(newTransformation: (Any) -> Any): MappingBuilder {
        val currentTransformation = this.transformation
        this.transformation = if (currentTransformation != null) {
            { value -> newTransformation(currentTransformation(value)) }
        } else {
            newTransformation
        }
        return this
    }

    /**
     * Apply a transformation to multiple field values.
     */
    fun transformM(transformation: (List<Any?>) -> Any): MappingBuilder {
        if (sourcePaths.size <= 1) {
            throw IllegalStateException("Use transform() for single field mappings")
        }

        this.multiFieldTransformation = transformation
        return this
    }


    /**
     * Specify a condition for when this mapping should be applied.
     */
    fun chooseIf(condition: (Any) -> Boolean): MappingBuilder {
        this.condition = condition
        return this
    }


    //-----------------------------------------------
    // String Transformations
    //-----------------------------------------------

    fun uppercase(): MappingBuilder = applyTransformer("uppercase")

    fun lowercase(): MappingBuilder = applyTransformer("lowercase")

    fun trim(): MappingBuilder = applyTransformer("trim")

    fun capitalize(): MappingBuilder = applyTransformer("capitalize")

    fun truncate(): MappingBuilder = applyTransformer("truncate")

    fun truncateLong(): MappingBuilder = applyTransformer("truncateLong")

    fun slugify(): MappingBuilder = applyTransformer("slugify")

    fun stripHtml(): MappingBuilder = applyTransformer("stripHtml")

    fun spacesToDashes(): MappingBuilder = applyTransformer("spacesToDashes")

    fun newlinesToBreaks(): MappingBuilder = applyTransformer("newlinesToBreaks")

    fun padLeft10(): MappingBuilder = applyTransformer("padLeft10")

    fun padRight20(): MappingBuilder = applyTransformer("padRight20")

    fun mask(): MappingBuilder = applyTransformer("mask")

    fun titleCase(): MappingBuilder = applyTransformer("titleCase")

    fun camelCase(): MappingBuilder = applyTransformer("camelCase")

    fun snakeCase(): MappingBuilder = applyTransformer("snakeCase")

    fun kebabCase(): MappingBuilder = applyTransformer("kebabCase")

    fun normalizeEmail(): MappingBuilder = applyTransformer("normalizeEmail")


    //-----------------------------------------------
    // Number Transformations
    //-----------------------------------------------

    fun increment(): MappingBuilder = applyTransformer("increment")

    fun times(multiplier: Double): MappingBuilder {
        val transformer = MultiplyBy(multiplier)
        println()
        return transform(transformer::transform)
    }

    fun decrement(): MappingBuilder = applyTransformer("decrement")

    fun toPercentage(): MappingBuilder = applyTransformer("toPercentage")

    fun fromPercentage(): MappingBuilder = applyTransformer("fromPercentage")

    fun round(): MappingBuilder = applyTransformer("round")

    fun floor(): MappingBuilder = applyTransformer("floor")

    fun ceil(): MappingBuilder = applyTransformer("ceil")

    fun abs(): MappingBuilder = applyTransformer("abs")

    fun formatNumber(): MappingBuilder = applyTransformer("formatNumber")

    fun formatUSD(): MappingBuilder = applyTransformer("formatUSD")

    fun formatEUR(): MappingBuilder = applyTransformer("formatEUR")

    fun formatGBP(): MappingBuilder = applyTransformer("formatGBP")

    fun roundToTwoDecimals(): MappingBuilder = applyTransformer("roundToTwoDecimals")

    fun roundToWholeNumber(): MappingBuilder = applyTransformer("roundToWholeNumber")

    //-----------------------------------------------
    // Date Transformations
    //-----------------------------------------------

    fun formatDateLong(): MappingBuilder = applyTransformer("formatDateLong")

    fun formatDateShort(): MappingBuilder = applyTransformer("formatDateShort")

    fun formatDateTime(): MappingBuilder = applyTransformer("formatDateTime")

    fun dateToTimestamp(): MappingBuilder = applyTransformer("dateToTimestamp")

    fun timestampToDate(): MappingBuilder = applyTransformer("timestampToDate")

    fun addOneDay(): MappingBuilder = applyTransformer("addOneDay")

    fun addOneWeek(): MappingBuilder = applyTransformer("addOneWeek")

    fun addOneMonth(): MappingBuilder = applyTransformer("addOneMonth")

    fun addOneYear(): MappingBuilder = applyTransformer("addOneYear")

    fun extractYear(): MappingBuilder = applyTransformer("extractYear")

    fun extractMonth(): MappingBuilder = applyTransformer("extractMonth")

    fun extractDay(): MappingBuilder = applyTransformer("extractDay")

    fun toIsoDate(): MappingBuilder = applyTransformer("toIsoDate")

    fun toIsoDateTime(): MappingBuilder = applyTransformer("toIsoDateTime")

    fun formatRelativeDate(): MappingBuilder = applyTransformer("formatRelativeDate")

    //-----------------------------------------------
    // Boolean Transformations
    //-----------------------------------------------

    fun negate(): MappingBuilder = applyTransformer("negate")

    fun booleanToString(): MappingBuilder = applyTransformer("booleanToString")

    fun toYesNo(): MappingBuilder = applyTransformer("toYesNo")

    fun toEnabledDisabled(): MappingBuilder = applyTransformer("toEnabledDisabled")

    fun toActiveInactive(): MappingBuilder = applyTransformer("toActiveInactive")

    fun booleanToNumber(): MappingBuilder = applyTransformer("booleanToNumber")

    fun stringToBoolean(): MappingBuilder = applyTransformer("stringToBoolean")

    //-----------------------------------------------
    // Collection Transformations
    //-----------------------------------------------

    fun join(): MappingBuilder = applyTransformer("join")

    fun joinComma(): MappingBuilder = applyTransformer("joinComma")

    fun joinPipe(): MappingBuilder = applyTransformer("joinPipe")

    fun size(): MappingBuilder = applyTransformer("size")

    fun first(): MappingBuilder = applyTransformer("first")

    fun last(): MappingBuilder = applyTransformer("last")

    fun sort(): MappingBuilder = applyTransformer("sort")

    fun reverse(): MappingBuilder = applyTransformer("reverse")

    fun filterNulls(): MappingBuilder = applyTransformer("filterNulls")

    fun split(): MappingBuilder = applyTransformer("split")

    fun splitComma(): MappingBuilder = applyTransformer("splitComma")

    fun mapToName(): MappingBuilder = applyTransformer("mapToName")

    //-----------------------------------------------
    // Conditional Transformations
    //-----------------------------------------------

    fun defaultIfNull(): MappingBuilder = applyTransformer("defaultIfNull")

    fun defaultIfEmpty(): MappingBuilder = applyTransformer("defaultIfEmpty")

    fun ifNull(): MappingBuilder = applyTransformer("ifNull")

    fun ifEmpty(): MappingBuilder = applyTransformer("ifEmpty")

    fun isNull(): MappingBuilder = applyTransformer("isNull")

    fun isEmpty(): MappingBuilder = applyTransformer("isEmpty")

    fun isNumber(): MappingBuilder = applyTransformer("isNumber")

    fun equalsTest(): MappingBuilder = applyTransformer("equalsTest")

    fun containsTest(): MappingBuilder = applyTransformer("containsTest")

    fun startsWithPrefix(): MappingBuilder = applyTransformer("startsWithPrefix")

    fun endsWithSuffix(): MappingBuilder = applyTransformer("endsWithSuffix")

    //-----------------------------------------------
    // Type Conversion Transformations
    //-----------------------------------------------

    fun stringy(): MappingBuilder = applyTransformer("toString")

    fun toInt(): MappingBuilder = applyTransformer("toInt")

    fun toDouble(): MappingBuilder = applyTransformer("toDouble")

    fun toBoolean(): MappingBuilder = applyTransformer("toBoolean")

    fun parseJson(): MappingBuilder = applyTransformer("parseJson")

    fun toJsonString(): MappingBuilder = applyTransformer("toJsonString")

    fun toBase64(): MappingBuilder = applyTransformer("toBase64")

    fun fromBase64(): MappingBuilder = applyTransformer("fromBase64")

    fun urlEncode(): MappingBuilder = applyTransformer("urlEncode")

    fun urlDecode(): MappingBuilder = applyTransformer("urlDecode")

    //-----------------------------------------------
    // Composite Transformations
    //-----------------------------------------------

    fun trimAndUppercase(): MappingBuilder = trim().uppercase()

    fun trimAndLowercase(): MappingBuilder = trim().lowercase()

    fun normalizeEmailAndMask(): MappingBuilder = normalizeEmail().mask()


}