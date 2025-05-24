package xyz.mahmoudahmed.logging

object LoggingConfig {
    var minLevel = LogLevel.INFO
    var formatter: LogFormatter = DefaultLogFormatter()
    val destinations: MutableList<LogDestination> = mutableListOf(ConsoleLogDestination())
    val interceptors = mutableListOf<LogInterceptor>()

    fun reset() {
        minLevel = LogLevel.INFO
        formatter = DefaultLogFormatter()
        destinations.clear()
        destinations.add(ConsoleLogDestination())
        interceptors.clear()
    }
}