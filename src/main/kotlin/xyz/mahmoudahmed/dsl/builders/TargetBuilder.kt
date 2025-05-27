package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.dsl.bulk.BulkMappingBuilder
import xyz.mahmoudahmed.dsl.collections.ForEachBuilder
import xyz.mahmoudahmed.dsl.conditional.BranchBuilder
import xyz.mahmoudahmed.dsl.core.Mapping
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.core.SetPropertyRule
import xyz.mahmoudahmed.dsl.structure.NestingBuilder
import xyz.mahmoudahmed.format.Format
import xyz.mahmoudahmed.logging.LogLevel
import xyz.mahmoudahmed.logging.LogMappingRule
import xyz.mahmoudahmed.logging.LoggingDslBuilder

class TargetBuilder internal constructor(
    val sourceName: String,
    private val sourceFormat: Format,
    val targetName: String,
    private val targetFormat: Format = Format.JSON,
    private val rules: MutableList<MappingRule> = mutableListOf()

) {
    val properties = mutableMapOf<String, Any>()

    fun withFormat(format: Format): TargetBuilder =
        TargetBuilder(sourceName, sourceFormat, targetName, format, rules)

    /**
     * Maps a single source field to a target.
     */
    fun map(sourcePath: String): MappingBuilder =
        MappingBuilder(this, listOf(sourcePath))

    /**
     * Maps multiple source fields to be combined.
     */
    fun map(vararg sourcePaths: String): MappingBuilder =
        MappingBuilder(this, sourcePaths.toList())



    /**
     * Map all fields that match a pattern.
     */
    fun mapAll(sourcePattern: String): BulkMappingBuilder =
        BulkMappingBuilder(this, sourcePattern)

    /**
     * Map all fields except those that match the excluded patterns.
     */
    fun mapAllExcept(vararg excludePatterns: String): BulkMappingBuilder =
        BulkMappingBuilder(this, "*").excluding(*excludePatterns)

    /**
     * Group fields that match a pattern into a nested structure.
     */
    fun nest(sourcePattern: String): NestingBuilder =
        NestingBuilder(this, sourcePattern)

    /**
     * Flatten a nested structure.
     */
    fun flatten(sourcePath: String): BulkMappingBuilder =
        BulkMappingBuilder(this, sourcePath).flatten()

    fun forEach(collectionPath: String): ForEachBuilder =
        ForEachBuilder(this, collectionPath)

    fun branch(): BranchBuilder = BranchBuilder(this)

    // Build method to create the final mapping
    fun build(): Mapping = Mapping(
        sourceName,
        sourceFormat,
        targetName,
        targetFormat,
        rules.toList(),
        properties.toMap()
    )

    // Internal method to add a rule - now thread-safe
    internal fun addRule(rule: MappingRule): TargetBuilder {
        rules.add(rule)
        return this
    }

    fun withLogging(init: LoggingDslBuilder.() -> Unit): TargetBuilder {
        val logBuilder = LoggingDslBuilder()
        logBuilder.init()
        // Store logging configuration in properties for later use
        this.properties["loggingConfig"] = logBuilder.build()
        return this
    }

    fun log(message: String, level: LogLevel = LogLevel.INFO): TargetBuilder {
        this.addRule(LogMappingRule(message, level))
        return this
    }

    fun logData(
        message: String,
        vararg paths: String,
        level: LogLevel = LogLevel.INFO
    ): TargetBuilder {
        this.addRule(LogMappingRule(message, level, paths.toList()))
        return this
    }

    fun trace(message: String): TargetBuilder = log(message, LogLevel.TRACE)
    fun debug(message: String): TargetBuilder = log(message, LogLevel.DEBUG)
    fun info(message: String): TargetBuilder = log(message, LogLevel.INFO)
    fun warn(message: String): TargetBuilder = log(message, LogLevel.WARN)
    fun error(message: String): TargetBuilder = log(message, LogLevel.ERROR)

    /**
     * Sets a property in the mapping context.
     *
     * @param propertyName The name of the property to set
     * @param valuePath The path to the value in the source data
     * @return This builder for method chaining
     */
    fun setProperty(propertyName: String, valuePath: String): TargetBuilder {
        this.addRule(SetPropertyRule(propertyName, valuePath))
        return this
    }
}
