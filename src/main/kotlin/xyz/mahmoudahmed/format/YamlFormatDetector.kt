package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets

/**
 * Implementation of YAML format detector
 */
class YamlFormatDetector : FormatDetector {
    override val formatType = FormatType.YAML

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        val text = String(data, StandardCharsets.UTF_8)
        val lines = text.lines().take(10)

        if (lines.isEmpty()) return 0.0f

        // Look for typical YAML patterns
        val hasIndentation = lines.any { it.startsWith("  ") || it.startsWith("\t") }
        val hasKeyValuePairs = lines.any { it.contains(": ") }
        val hasDashes = lines.any { it.trim().startsWith("- ") }

        // Combine signals
        var confidence = 0.0f
        if (hasKeyValuePairs) confidence += 0.4f
        if (hasIndentation) confidence += 0.3f
        if (hasDashes) confidence += 0.3f

        return confidence.coerceAtMost(0.9f) // Cap at 0.9
    }
}
