package xyz.mahmoudahmed.logging

interface LogFormatter {
    fun format(level: LogLevel, message: String, data: Any?): String
}