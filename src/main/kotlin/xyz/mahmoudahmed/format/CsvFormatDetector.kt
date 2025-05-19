package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets

/**
 * Implementation of CSV format detector
 */
class CsvFormatDetector : FormatDetector {
    override val formatType = FormatType.CSV

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        val text = String(data, StandardCharsets.UTF_8)
        val lines = text.lines().take(5) // Look at first 5 lines

        if (lines.isEmpty()) return 0.0f

        // Check if all lines have the same number of commas
        val commaCount = lines[0].count { it == ',' }
        if (commaCount == 0) return 0.1f  // Very low confidence if no commas

        // Higher confidence if consistent delimiter count and at least 2 rows
        var consistentDelimiters = true
        for (i in 1 until lines.size) {
            if (lines[i].count { it == ',' } != commaCount) {
                consistentDelimiters = false
                break
            }
        }

        return when {
            consistentDelimiters && lines.size > 1 -> 0.8f
            consistentDelimiters -> 0.6f
            else -> 0.3f
        }
    }
}

