package xyz.mahmoudahmed.logging

interface LogInterceptor {
    fun intercept(level: LogLevel, message: String, data: Any?): Boolean
}