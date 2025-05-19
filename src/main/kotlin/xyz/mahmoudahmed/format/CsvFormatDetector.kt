package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.ByteArrayInputStream

/**
 * CSV format detector with support for multiple
 * delimiters, quoted fields, and intelligent pattern analysis.
 */
class CsvFormatDetector : FormatDetector {
    override val formatType = FormatType.CSV


    private val possibleDelimiters = listOf(',', ';', '\t', '|')


    private val MAX_SAMPLE_LINES = 20

    private val MIN_FIELDS = 2

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        try {
            val reader = BufferedReader(InputStreamReader(
                ByteArrayInputStream(data), StandardCharsets.UTF_8))

            val sampleLines = reader.useLines { lines ->
                lines.filter { it.isNotBlank() }
                    .take(MAX_SAMPLE_LINES)
                    .toList()
            }

            if (sampleLines.isEmpty()) return 0.0f

            val (bestDelimiter, bestScore) = findBestDelimiter(sampleLines)

            if (bestScore < 0.4f) return bestScore

            return calculateConfidenceScore(sampleLines, bestDelimiter)

        } catch (e: Exception) {
            return 0.1f
        }
    }

    /**
     * Analyzes sample lines to find the most likely delimiter.
     * @return Pair of (best delimiter, confidence score)
     */
    private fun findBestDelimiter(lines: List<String>): Pair<Char, Float> {
        var bestDelimiter = ','
        var bestScore = 0.0f

        for (delimiter in possibleDelimiters) {
            val fieldCounts = lines.map { line ->
                countFields(line, delimiter)
            }

            if (fieldCounts.mostFrequent() <= MIN_FIELDS) continue

            val mostCommonCount = fieldCounts.mostFrequent()
            val consistencyRatio = fieldCounts.count { it == mostCommonCount } /
                    fieldCounts.size.toFloat()

            val delimiterScore = consistencyRatio *
                    (0.5f + Math.min(0.5f, (mostCommonCount - 1) * 0.1f))

            if (delimiterScore > bestScore) {
                bestScore = delimiterScore
                bestDelimiter = delimiter
            }
        }

        return Pair(bestDelimiter, bestScore)
    }

    /**
     * Calculates a comprehensive confidence score based on multiple CSV characteristics.
     */
    private fun calculateConfidenceScore(lines: List<String>, delimiter: Char): Float {
        val fieldCounts = lines.map { countFields(it, delimiter) }
        val mostCommonCount = fieldCounts.mostFrequent()
        val consistencyRatio = fieldCounts.count { it == mostCommonCount } /
                fieldCounts.size.toFloat()

        var score = consistencyRatio * (if (lines.size == 1) 0.5f else 0.7f)

        score += Math.min(0.1f, mostCommonCount * 0.015f)

        if (lines.size > 1) {
            score += Math.min(0.15f, lines.size * 0.01f)

            val potentialHeader = lines[0]
            val secondRow = lines[1]

            val headerAnalysis = analyzeFieldTypes(potentialHeader, delimiter)
            val dataAnalysis = analyzeFieldTypes(secondRow, delimiter)

            val headerHasMoreText = headerAnalysis.textRatio > dataAnalysis.textRatio

            val dataRowHasNumbers = dataAnalysis.numericCount > 0

            if (headerHasMoreText && dataRowHasNumbers) {
                score += 0.1f
            }
        } else {
            if (mostCommonCount <= 5) {
                score -= 0.1f
            }
        }

        val hasQuotedFields = lines.any { line ->
            line.contains("\"") && line.count { it == '"' } % 2 == 0
        }

        if (hasQuotedFields) score += 0.1f

        return score.coerceIn(0.0f, 1.0f)
    }
    /**
     * Counts fields in a line taking quoted sections into account.
     */
    private fun countFields(line: String, delimiter: Char): Int {
        var count = 1
        var inQuotes = false

        for (i in line.indices) {
            val c = line[i]

            if (c == '"' && (i == 0 || line[i-1] != '\\')) {
                inQuotes = !inQuotes
            }
            else if (c == delimiter && !inQuotes) {
                count++
            }
        }

        return count
    }

    /**
     * Analyzes the types of fields in a line.
     * @return FieldAnalysis containing counts and ratios of different field types
     */
    private fun analyzeFieldTypes(line: String, delimiter: Char): FieldAnalysis {
        val fields = parseFields(line, delimiter)

        var textCount = 0
        var numericCount = 0
        var emptyCount = 0

        for (field in fields) {
            val trimmed = field.trim()
            when {
                trimmed.isEmpty() -> emptyCount++
                trimmed.matches(Regex("-?\\d+(\\.\\d+)?")) -> numericCount++
                else -> textCount++
            }
        }

        val total = fields.size.toFloat()
        return FieldAnalysis(
            textCount = textCount,
            numericCount = numericCount,
            emptyCount = emptyCount,
            textRatio = textCount / total,
            numericRatio = numericCount / total,
            emptyRatio = emptyCount / total
        )
    }

    /**
     * Parses fields from a line considering quoted fields.
     */
    private fun parseFields(line: String, delimiter: Char): List<String> {
        val fields = mutableListOf<String>()
        var currentField = StringBuilder()
        var inQuotes = false

        for (i in line.indices) {
            val c = line[i]


            if (c == '"' && (i == 0 || line[i-1] != '\\')) {
                inQuotes = !inQuotes
                currentField.append(c)
            }
            else if (c == delimiter && !inQuotes) {
                fields.add(currentField.toString())
                currentField = StringBuilder()
            }
            else {
                currentField.append(c)
            }
        }

        fields.add(currentField.toString())

        return fields
    }

    /**
     * Data class to hold field type analysis results.
     */
    private data class FieldAnalysis(
        val textCount: Int,
        val numericCount: Int,
        val emptyCount: Int,
        val textRatio: Float,
        val numericRatio: Float,
        val emptyRatio: Float
    )

    /**
     * Extension function to find the most frequent value in a collection.
     */
    private fun <T> Collection<T>.mostFrequent(): T {
        return this.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
            ?: throw IllegalArgumentException("Collection cannot be empty")
    }
}