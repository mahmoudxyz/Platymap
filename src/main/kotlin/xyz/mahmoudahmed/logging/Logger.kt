package xyz.mahmoudahmed.logging

object Logger {
    fun log(level: LogLevel, message: String, data: Any? = null) {
        if (level.ordinal < LoggingConfig.minLevel.ordinal) return

        // Check if any interceptor blocks this log
        val shouldLog = LoggingConfig.interceptors.isEmpty() ||
                !LoggingConfig.interceptors.any { it.intercept(level, message, data) }

        if (shouldLog) {
            val formattedMessage = LoggingConfig.formatter.format(level, message, data)
            LoggingConfig.destinations.forEach { it.log(formattedMessage) }
        }
    }

    fun trace(message: String, data: Any? = null) = log(LogLevel.TRACE, message, data)
    fun debug(message: String, data: Any? = null) = log(LogLevel.DEBUG, message, data)
    fun info(message: String, data: Any? = null) = log(LogLevel.INFO, message, data)
    fun warn(message: String, data: Any? = null) = log(LogLevel.WARN, message, data)
    fun error(message: String, data: Any? = null) = log(LogLevel.ERROR, message, data)
}