package xyz.mahmoudahmed.logging


class LoggingDslBuilder {
    private var minLevel = LogLevel.INFO
    private var includeData = false
    private var formatter: LogFormatter = DefaultLogFormatter()
    private val destinations = mutableListOf<LogDestination>()

    fun level(level: LogLevel) {
        this.minLevel = level
    }

    fun includeDataInLogs(include: Boolean = true) {
        this.includeData = include
    }

    fun useFormatter(formatter: LogFormatter) {
        this.formatter = formatter
    }

    fun logToConsole() {
        destinations.add(ConsoleLogDestination())
    }

    fun logToFile(filePath: String) {
        destinations.add(FileLogDestination(filePath))
    }

    fun logToCustomDestination(destination: LogDestination) {
        destinations.add(destination)
    }

    internal fun build(): LoggingConfiguration {
        return LoggingConfiguration(
            minLevel = minLevel,
            includeData = includeData,
            formatter = formatter,
            destinations = destinations.toList()
        )
    }
}