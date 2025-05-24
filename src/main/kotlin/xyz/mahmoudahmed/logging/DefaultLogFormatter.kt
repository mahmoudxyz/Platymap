package xyz.mahmoudahmed.logging

import java.text.SimpleDateFormat
import java.util.*

class DefaultLogFormatter : LogFormatter {
    override fun format(level: LogLevel, message: String, data: Any?): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val dataStr = data?.let { " - Data: $it" } ?: ""
        return "[$timestamp] ${level.name}: $message$dataStr"
    }
}