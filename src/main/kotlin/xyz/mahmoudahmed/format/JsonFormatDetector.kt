package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets
import java.util.Stack
import java.util.regex.Pattern
import kotlin.math.min
import kotlin.math.max

/**
 * JSON format detector with adaptive sampling,
 * intelligent structure analysis, and dynamic content validation.
 */
class JsonFormatDetector : FormatDetector {
    override val format = Format.JSON

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f
        return when {
            data.size <= SMALL_FILE_THRESHOLD -> analyzeCompleteContent(data)
            data.size <= MEDIUM_FILE_THRESHOLD -> analyzeSampledContent(data, MEDIUM_SAMPLING_RATE)
            else -> analyzeWithAdaptiveSampling(data)
        }
    }

    /**
     * Performs complete analysis on small files.
     */
    private fun analyzeCompleteContent(data: ByteArray): Float {
        try {
            val text = String(data, StandardCharsets.UTF_8).trim()
            return analyzeJsonContent(text, AnalysisDepth.FULL)
        } catch (e: Exception) {
            return handleException(e)
        }
    }

    /**
     * Analyzes content using uniform sampling for medium-sized files.
     */
    private fun analyzeSampledContent(data: ByteArray, samplingRate: Int): Float {
        try {
            val sampleSize = max(MIN_SAMPLE_SIZE, data.size / samplingRate)
            val sample = ByteArray(sampleSize)

            var sampleIndex = 0
            for (i in data.indices step samplingRate) {
                if (sampleIndex < sample.size) {
                    sample[sampleIndex++] = data[i]
                }
            }

            val suffixStart = max(0, data.size - (sample.size - sampleIndex))
            for (i in suffixStart until data.size) {
                if (sampleIndex < sample.size) {
                    sample[sampleIndex++] = data[i]
                }
            }

            val text = String(sample, StandardCharsets.UTF_8).trim()
            return analyzeJsonContent(text, AnalysisDepth.MEDIUM)
        } catch (e: Exception) {
            return handleException(e)
        }
    }

    /**
     * Uses adaptive sampling for large files, focusing on structure and key areas.
     */
    private fun analyzeWithAdaptiveSampling(data: ByteArray): Float {
        try {
            // Create a smart sample focusing on the beginning, end, and periodic samples
            val prefixSize = min(data.size / 3, LARGE_FILE_PREFIX_SIZE)
            val suffixSize = min(data.size / 4, LARGE_FILE_SUFFIX_SIZE)
            val periodicSampleSize = min(data.size / 10, LARGE_FILE_PERIODIC_SAMPLE_SIZE)

            val prefix = String(data.copyOfRange(0, prefixSize), StandardCharsets.UTF_8)
            val suffix = String(data.copyOfRange(max(0, data.size - suffixSize), data.size), StandardCharsets.UTF_8)

            if (!hasValidJsonStructure(prefix, suffix)) {
                return 0.0f
            }

            val stride = max(1, data.size / periodicSampleSize)
            val periodicSamples = StringBuilder()

            for (i in 0 until data.size step stride) {
                val chunkSize = min(CHUNK_SIZE, data.size - i)
                if (chunkSize > 0) {
                    periodicSamples.append(String(data.copyOfRange(i, i + chunkSize), StandardCharsets.UTF_8))
                    periodicSamples.append(" ")
                }
            }

            val patternScore = analyzePatterns(periodicSamples.toString())

            return (STRUCTURE_WEIGHT * 1.0f + PATTERN_WEIGHT * patternScore).toFloat()
        } catch (e: Exception) {
            return handleException(e)
        }
    }

    /**
     * Checks if prefix and suffix form a valid JSON structure.
     */
    private fun hasValidJsonStructure(prefix: String, suffix: String): Boolean {
        val trimmedPrefix = prefix.trim()
        val trimmedSuffix = suffix.trim()

        return (trimmedPrefix.startsWith("{") || trimmedPrefix.startsWith("[")) &&
                (trimmedSuffix.endsWith("}") || trimmedSuffix.endsWith("]"))
    }

    /**
     * Main analysis function that examines a JSON content with configurable depth.
     */
    private fun analyzeJsonContent(text: String, depth: AnalysisDepth): Float {
        if (!(text.startsWith("{") || text.startsWith("[")) ||
            !(text.endsWith("}") || text.endsWith("]"))) {
            return 0.0f
        }

        val structureScore = validateStructure(text)
        if (structureScore == 0.0f) return 0.0f

        val patternScore = analyzePatterns(text)

        val contentScore = when (depth) {
            AnalysisDepth.FULL -> analyzeContent(text)
            AnalysisDepth.MEDIUM -> analyzeContent(text.take(MEDIUM_CONTENT_SCAN_LIMIT))
            AnalysisDepth.MINIMAL -> 0.5f
        }

        return when (depth) {
            AnalysisDepth.FULL -> (structureScore * 0.4f + patternScore * 0.4f + contentScore * 0.2f)
                .coerceIn(0.0f, 1.0f)
            AnalysisDepth.MEDIUM -> (structureScore * 0.5f + patternScore * 0.3f + contentScore * 0.2f)
                .coerceIn(0.0f, 1.0f)
            AnalysisDepth.MINIMAL -> (structureScore * 0.6f + patternScore * 0.4f)
                .coerceIn(0.0f, 1.0f)
        }
    }

    /**
     * Validates the JSON structure using bracket matching with optimized character scanning.
     */
    private fun validateStructure(text: String): Float {
        val stack = Stack<Char>()
        var inString = false
        var escaped = false

        for (c in text) {
            if (escaped) {
                escaped = false
                continue
            }

            if (c == '\\' && inString) {
                escaped = true
                continue
            }

            if (c == '"') {
                inString = !inString
                continue
            }

            if (!inString) {
                when (c) {
                    '{', '[' -> stack.push(c)
                    '}' -> {
                        if (stack.isEmpty() || stack.pop() != '{') return 0.0f
                    }
                    ']' -> {
                        if (stack.isEmpty() || stack.pop() != '[') return 0.0f
                    }
                }
            }
        }

        return if (stack.isEmpty() && !inString) 1.0f else 0.0f
    }

    /**
     * Analyzes JSON specific patterns with attention to syntax elements.
     */
    private fun analyzePatterns(text: String): Float {
        var score = 0.0f
        val length = text.length

        if (length > 10) {
            if (KEY_VALUE_PATTERN.matcher(text).find()) {
                score += KEY_VALUE_PATTERN_WEIGHT
            }

            if (ARRAY_PATTERN.matcher(text).find()) {
                score += ARRAY_PATTERN_WEIGHT
            }

            if (text.contains("\":{") || text.contains("\":[")) {
                score += NESTED_STRUCTURE_WEIGHT
            }

            if (text.contains("true") || text.contains("false")) {
                score += BOOLEAN_VALUE_WEIGHT
            }

            if (text.contains("null")) {
                score += NULL_VALUE_WEIGHT
            }

            if (NUMERIC_PATTERN.matcher(text).find()) {
                score += NUMERIC_VALUE_WEIGHT
            }
        }

        return score.coerceIn(0.0f, 1.0f)
    }

    /**
     * Analyzes the actual content to detect JSON typical patterns.
     */
    private fun analyzeContent(text: String): Float {
        val score: Float

        val keyMatcher = KEY_PATTERN.matcher(text)
        val keys = mutableSetOf<String>()

        while (keyMatcher.find() && keys.size < MAX_KEYS_TO_ANALYZE) {
            keys.add(keyMatcher.group(1))
        }

        if (keys.isEmpty()) return 0.2f

        var commonKeyCount = 0

        if (keys.any { it.equals("id", ignoreCase = true) || it.endsWith("Id", ignoreCase = true) }) {
            commonKeyCount++
        }

        if (keys.any {
                it.equals("type", ignoreCase = true) ||
                        it.equals("name", ignoreCase = true) ||
                        it.equals("value", ignoreCase = true) ||
                        it.equals("data", ignoreCase = true) ||
                        it.contains("date", ignoreCase = true) ||
                        it.contains("time", ignoreCase = true)
            }) {
            commonKeyCount++
        }

        if (keys.any {
                it.endsWith("s", ignoreCase = true) && it.length > 2 ||
                        it.contains("list", ignoreCase = true) ||
                        it.contains("array", ignoreCase = true) ||
                        it.contains("items", ignoreCase = true) ||
                        it.contains("elements", ignoreCase = true)
            }) {
            commonKeyCount++
        }

        score = min(1.0f, commonKeyCount * 0.15f + 0.2f)

        return score
    }

    /**
     * Handles exceptions with proper logging and recovery.
     */
    private fun handleException(e: Exception): Float {
        return 0.0f
    }

    /**
     * Analysis depth levels for different file sizes.
     */
    private enum class AnalysisDepth {
        FULL,
        MEDIUM,
        MINIMAL
    }

    companion object {
        // File size thresholds
        private const val SMALL_FILE_THRESHOLD = 16_384
        private const val MEDIUM_FILE_THRESHOLD = 1_048_576

        // Sampling parameters
        private const val MEDIUM_SAMPLING_RATE = 10
        private const val MIN_SAMPLE_SIZE = 4_096
        private const val LARGE_FILE_PREFIX_SIZE = 4_096
        private const val LARGE_FILE_SUFFIX_SIZE = 2_048
        private const val LARGE_FILE_PERIODIC_SAMPLE_SIZE = 8_192
        private const val CHUNK_SIZE = 64

        // Analysis limits
        private const val MEDIUM_CONTENT_SCAN_LIMIT = 10_000
        private const val MAX_KEYS_TO_ANALYZE = 50

        // Scoring weights
        private const val STRUCTURE_WEIGHT = 0.6
        private const val PATTERN_WEIGHT = 0.4
        private const val KEY_VALUE_PATTERN_WEIGHT = 0.4f
        private const val ARRAY_PATTERN_WEIGHT = 0.2f
        private const val NESTED_STRUCTURE_WEIGHT = 0.2f
        private const val BOOLEAN_VALUE_WEIGHT = 0.1f
        private const val NULL_VALUE_WEIGHT = 0.05f
        private const val NUMERIC_VALUE_WEIGHT = 0.1f

        // Pattern matchers (compiled once for performance)
        private val KEY_VALUE_PATTERN = Pattern.compile("\"[^\"]+\"\\s*:\\s*[\\{\\[\"\\d\\.tfn]")
        private val ARRAY_PATTERN = Pattern.compile("\\[\\s*([\"\\d\\.tfn]|\\{)")
        private val NUMERIC_PATTERN = Pattern.compile(":\\s*-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?")
        private val KEY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:")
    }
}