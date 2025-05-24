package xyz.mahmoudahmed.logging

import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule

class LogMappingRule(
    private val message: String,
    private val level: LogLevel,
    private val dataPaths: List<String> = emptyList()
) : MappingRule {
    override fun apply(context: MappingContext, target: Any) {
        // Extract data if paths are specified
        val dataToLog = if (dataPaths.isNotEmpty()) {
            dataPaths.associateWith { path ->
                context.getValueByPath(path)
            }
        } else {
            // If no paths specified, check if we should log the context
            val loggingConfig = context.properties["loggingConfig"] as? LoggingConfiguration
            if (loggingConfig?.includeData == true) {
                mapOf("source" to context.sourceData, "target" to target)
            } else {
                null
            }
        }

        // Get logging configuration or use global config
        val config = context.properties["loggingConfig"] as? LoggingConfiguration
        if (config != null) {
            // Use custom configuration
            if (level.ordinal >= config.minLevel.ordinal) {
                val formattedMessage = config.formatter.format(level, message, dataToLog)
                config.destinations.forEach { it.log(formattedMessage) }
            }
        } else {
            // Use global logger
            Logger.log(level, message, dataToLog)
        }
    }
}