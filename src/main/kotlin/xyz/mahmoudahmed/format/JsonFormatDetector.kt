package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets

/**
 * Implementation of JSON format detector
 */
class JsonFormatDetector : FormatDetector {
    override val formatType = FormatType.JSON

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        val text = String(data, StandardCharsets.UTF_8).trim()

        // Check if it starts with { or [ and ends with } or ]
        val isValidStart = text.startsWith("{") || text.startsWith("[")
        val isValidEnd = text.endsWith("}") || text.endsWith("]")

        if (isValidStart && isValidEnd) {
            // Simple heuristic: look for typical JSON patterns
            val hasQuotedStrings = text.contains("\"")
            val hasColons = text.contains(":")
            val hasCommas = text.contains(",")

            return if (hasQuotedStrings && hasColons && hasCommas) 0.9f else 0.5f
        }

        return 0.0f
    }
}