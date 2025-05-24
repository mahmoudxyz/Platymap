package xyz.mahmoudahmed.logging

data class LoggingConfiguration(
    val minLevel: LogLevel,
    val includeData: Boolean,
    val formatter: LogFormatter,
    val destinations: List<LogDestination>
)