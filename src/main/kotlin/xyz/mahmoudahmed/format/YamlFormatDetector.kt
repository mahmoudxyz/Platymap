package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * YAML format detector with improved accuracy in functional programming style
 */
class YamlFormatDetector : FormatDetector {
    override val formatType = FormatType.YAML

    private val keyValuePattern = Pattern.compile("^\\s*[\\w.-]+\\s*:\\s*.*$")
    private val listItemPattern = Pattern.compile("^\\s*-\\s+.*$")
    private val simpleListItemPattern = Pattern.compile("^\\s*-\\s*$")
    private val commentPattern = Pattern.compile("^\\s*#.*$")
    private val documentStartPattern = Pattern.compile("^---(?:\\s.*)?$")
    private val documentEndPattern = Pattern.compile("^\\.\\.\\.$")
    private val anchorPattern = Pattern.compile("&[\\w.-]+")
    private val aliasPattern = Pattern.compile("\\*[\\w.-]+")
    private val tagPattern = Pattern.compile("![\\w/.-]+")
    private val multilineStringPattern = Pattern.compile("^\\s*[\\w.-]+\\s*:\\s*[|>][+-]?\\s*$")
    private val continuationPattern = Pattern.compile("^\\s{2,}.*$")
    private val gitHubActionsOnPattern = Pattern.compile("^\\s*on\\s*:")
    private val gitHubActionsJobsPattern = Pattern.compile("^\\s*jobs\\s*:")
    private val gitHubActionsStepsPattern = Pattern.compile("^\\s+steps\\s*:")
    private val gitHubActionsUsesPattern = Pattern.compile("^\\s+uses\\s*:")
    private val gitHubActionsRunsOnPattern = Pattern.compile("^\\s+runs-on\\s*:")

    // Common YAML keys as immutable set
    private val commonYamlKeys = setOf(
        "version", "apiVersion", "kind", "metadata", "spec", "name",
        "description", "environment", "dependencies", "services",
        "volumes", "config", "data", "type", "image", "labels", "annotations",
        "resources", "ports", "host", "path", "command", "args", "value",
        "on", "jobs", "runs-on", "steps", "uses", "with", "env",
        "permissions", "needs", "if", "matrix", "workflow_dispatch",
        "push", "pull_request", "branches", "paths", "outputs", "run"
    )

    // Non-YAML indicators as immutable list
    private val nonYamlIndicators = listOf(
        Pattern.compile("<\\?xml\\s+version="),
        Pattern.compile("<!DOCTYPE\\s+html"),
        Pattern.compile("<html[\\s>]"),
        Pattern.compile("<body[\\s>]"),
        Pattern.compile("<script[\\s>]"),
        Pattern.compile("^\\s*public\\s+class")
    )

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        val text = convertBytesToString(data)
        val lines = splitIntoLines(text)

        if (lines.isEmpty()) return 0.0f

        return calculateYamlConfidence(lines, text)
    }

    // Pure function to convert bytes to string
    private fun convertBytesToString(data: ByteArray): String {
        return if (data.isEmpty()) "" else String(data, StandardCharsets.UTF_8)
    }

    // Pure function to split text into lines
    private fun splitIntoLines(text: String): List<String> {
        return text.lines()
    }

    // Function to calculate overall YAML confidence
    private fun calculateYamlConfidence(lines: List<String>, text: String): Float {
        val strongIndicatorConfidence = detectStrongYamlIndicators(lines)
        if (strongIndicatorConfidence > 0.9f) {
            return 0.92f
        }

        val gitHubActionsConfidence = detectGitHubActionsYaml(lines)
        if (gitHubActionsConfidence > 0.8f) {
            return 0.9f
        }

        val yamlFeaturesCount = countYamlFeatures(lines)
        val nonYamlConfidence = calculateNonYamlConfidence(lines)

        if (nonYamlConfidence > 0.8f && yamlFeaturesCount < 5) {
            return 0.0f
        }

        val structureConfidence = calculateStructureConfidence(lines)
        val contentConfidence = calculateContentConfidence(lines, text)
        val indentationConfidence = calculateIndentationConfidence(lines)
        val markerConfidence = calculateMarkerConfidence(lines)

        val combinedConfidence = combineConfidences(
            structureConfidence,
            contentConfidence,
            indentationConfidence,
            markerConfidence,
            nonYamlConfidence
        )

        val adjustedConfidence = adjustConfidenceByFeatureCount(combinedConfidence, yamlFeaturesCount)
        val finalConfidence = applyConfidenceAdjustments(adjustedConfidence, lines, text)

        return finalConfidence.coerceIn(0.0f, 0.95f)
    }

    // Pure function to combine confidence scores
    private fun combineConfidences(
        structureConfidence: Float,
        contentConfidence: Float,
        indentationConfidence: Float,
        markerConfidence: Float,
        nonYamlConfidence: Float
    ): Float {
        val baseConfidence = (
                structureConfidence * 0.4f +
                        contentConfidence * 0.3f +
                        indentationConfidence * 0.15f +
                        markerConfidence * 0.15f
                )

        return if (nonYamlConfidence > 0.0f) {
            baseConfidence * (1.0f - (nonYamlConfidence * 0.3f)).coerceAtLeast(0.5f)
        } else {
            baseConfidence
        }
    }

    // Pure function to adjust confidence based on feature count
    private fun adjustConfidenceByFeatureCount(confidence: Float, featureCount: Int): Float {
        return when {
            featureCount > 10 -> (confidence + 0.15f).coerceAtMost(0.95f)
            featureCount > 5 -> (confidence + 0.1f).coerceAtMost(0.95f)
            else -> confidence
        }
    }

    // Check for strong YAML indicators
    private fun detectStrongYamlIndicators(lines: List<String>): Float {
        val hasDocumentStart = lines.any { documentStartPattern.matcher(it).matches() }
        val hasDocumentEnd = lines.any { documentEndPattern.matcher(it).matches() }
        val hasAnchors = lines.any { anchorPattern.matcher(it).find() }
        val hasAliases = lines.any { aliasPattern.matcher(it).find() }
        val multilineStringCount = countMultilineStrings(lines)

        return calculateStrongIndicatorConfidence(
            hasDocumentStart,
            hasDocumentEnd,
            hasAnchors,
            hasAliases,
            multilineStringCount
        )
    }

    // Count multiline string patterns
    private fun countMultilineStrings(lines: List<String>): Int {
        return lines.count { multilineStringPattern.matcher(it).matches() }
    }

    // Calculate confidence based on strong indicators
    private fun calculateStrongIndicatorConfidence(
        hasDocumentStart: Boolean,
        hasDocumentEnd: Boolean,
        hasAnchors: Boolean,
        hasAliases: Boolean,
        multilineStringCount: Int
    ): Float {
        val documentMarkerConfidence = when {
            hasDocumentStart && hasDocumentEnd -> 0.95f
            hasDocumentStart -> 0.85f
            else -> 0.0f
        }

        val anchorAliasConfidence = when {
            hasAnchors && hasAliases -> 0.9f
            hasAnchors || hasAliases -> 0.85f
            else -> 0.0f
        }

        val multilineConfidence = when {
            multilineStringCount >= 2 -> 0.9f
            multilineStringCount == 1 -> 0.8f
            else -> 0.0f
        }

        return maxOf(documentMarkerConfidence, anchorAliasConfidence, multilineConfidence)
    }

    // Detect GitHub Actions YAML
    private fun detectGitHubActionsYaml(lines: List<String>): Float {
        val gitHubActionsMarkers = countGitHubActionsMarkers(lines)
        val hasOn = lines.any { it.trim().startsWith("on:") }
        val hasJobs = lines.any { it.trim().startsWith("jobs:") }

        return calculateGitHubActionsConfidence(gitHubActionsMarkers, hasOn, hasJobs)
    }

    // Count GitHub Actions specific markers
    private fun countGitHubActionsMarkers(lines: List<String>): Int {
        return lines.sumOf { line ->
            countMarkerInLine(line)
        }
    }

    // Count markers in a single line
    private fun countMarkerInLine(line: String): Int {
        var count = 0
        if (gitHubActionsOnPattern.matcher(line).find()) count++
        if (gitHubActionsJobsPattern.matcher(line).find()) count++
        if (gitHubActionsStepsPattern.matcher(line).find()) count++
        if (gitHubActionsUsesPattern.matcher(line).find()) count++
        if (gitHubActionsRunsOnPattern.matcher(line).find()) count++
        if (line.contains("github.com") || line.contains("actions/")) count++
        return count
    }

    // Calculate GitHub Actions confidence
    private fun calculateGitHubActionsConfidence(markerCount: Int, hasOn: Boolean, hasJobs: Boolean): Float {
        val adjustedCount = if (hasOn && hasJobs) markerCount + 3 else markerCount

        return when {
            adjustedCount >= 5 -> 0.95f
            adjustedCount >= 3 -> 0.85f
            adjustedCount >= 2 -> 0.7f
            adjustedCount >= 1 -> 0.5f
            else -> 0.0f
        }
    }

    // Calculate non-YAML confidence
    private fun calculateNonYamlConfidence(lines: List<String>): Float {
        val relevantLines = lines.filter { it.trim().isNotEmpty() }.take(10)
        val nonYamlHits = countNonYamlHits(relevantLines)

        return when {
            nonYamlHits >= 5 -> 0.9f
            nonYamlHits >= 3 -> 0.7f
            nonYamlHits >= 2 -> 0.5f
            nonYamlHits >= 1 -> 0.3f
            else -> 0.0f
        }
    }

    // Count non-YAML indicators
    private fun countNonYamlHits(lines: List<String>): Int {
        val patternHits = lines.sumOf { line ->
            nonYamlIndicators.count { pattern -> pattern.matcher(line).find() }
        }

        val xmlHits = lines.count { it.contains("<?") && it.contains("?>") }
        val htmlHits = lines.count { it.contains("<") && it.contains(">") && !it.contains(":") }
        val jsonObjectHits = lines.count { it.contains("{") && it.contains("}") && it.contains(":\"") }
        val jsonArrayHits = lines.count { it.contains("[") && it.contains("]") && it.contains(",") }

        return patternHits + xmlHits + htmlHits + jsonObjectHits + jsonArrayHits
    }

    // Count YAML features
    private fun countYamlFeatures(lines: List<String>): Int {
        return lines.sumOf { line -> countYamlFeaturesInLine(line) }
    }

    // Count YAML features in a single line (continued)
    private fun countYamlFeaturesInLine(line: String): Int {
        var count = 0

        // Basic structural elements
        if (keyValuePattern.matcher(line).matches()) count++
        if (listItemPattern.matcher(line).matches()) count++
        if (simpleListItemPattern.matcher(line).matches()) count++
        if (commentPattern.matcher(line).matches()) count++

        // Document markers
        if (documentStartPattern.matcher(line).matches()) count += 2
        if (documentEndPattern.matcher(line).matches()) count += 2

        // YAML-specific features
        if (anchorPattern.matcher(line).find()) count += 2
        if (aliasPattern.matcher(line).find()) count += 2
        if (tagPattern.matcher(line).find()) count += 2
        if (multilineStringPattern.matcher(line).matches()) count += 2

        // Likely YAML format indicators
        if (line.contains(": ")) count++
        if (line.trim().startsWith("-") && !line.contains("->")) count++

        // Common YAML keys
        if (commonYamlKeys.any { line.trim().startsWith("$it:") }) count++

        return count
    }

    // Calculate structure confidence
    private fun calculateStructureConfidence(lines: List<String>): Float {
        val relevantLines = lines.filter { it.trim().isNotEmpty() }
        if (relevantLines.isEmpty()) return 0.0f

        val counts = countStructuralElements(relevantLines)
        return calculateStructureScore(counts, relevantLines.size)
    }

    // Count structural elements in lines
    private fun countStructuralElements(lines: List<String>): Triple<Int, Int, Int> {
        var keyValueCount = 0
        var listItemCount = 0
        var commentCount = 0

        lines.forEach { line ->
            if (keyValuePattern.matcher(line).matches()) keyValueCount++
            if (listItemPattern.matcher(line).matches() || simpleListItemPattern.matcher(line).matches()) listItemCount++
            if (commentPattern.matcher(line).matches()) commentCount++
        }

        return Triple(keyValueCount, listItemCount, commentCount)
    }

    // Calculate structure score based on counts
    private fun calculateStructureScore(counts: Triple<Int, Int, Int>, totalLines: Int): Float {
        val (keyValueCount, listItemCount, commentCount) = counts

        val structureRatio = (keyValueCount + listItemCount).toFloat() / totalLines

        val commentBoost = when {
            commentCount >= 3 -> 0.2f
            commentCount > 0 -> 0.1f
            else -> 0.0f
        }

        val patternBoost = when {
            keyValueCount > 10 || listItemCount > 10 -> 0.15f
            keyValueCount > 5 || listItemCount > 5 -> 0.1f
            else -> 0.0f
        }

        return (structureRatio * 0.7f + commentBoost + patternBoost).coerceIn(0.0f, 1.0f)
    }

    // Calculate content confidence
    private fun calculateContentConfidence(lines: List<String>, text: String): Float {
        val contentFeatures = extractContentFeatures(lines, text)
        return calculateContentScore(contentFeatures)
    }

    // Class to hold content feature counts
    private data class ContentFeatures(
        val commonKeyCount: Int,
        val anchorAliasCount: Int,
        val tagCount: Int,
        val multilineStringCount: Int,
        val multilineContentLines: Int
    )

    // Extract content features from lines
    private fun extractContentFeatures(lines: List<String>, text: String): ContentFeatures {
        var commonKeyCount = 0
        var anchorAliasCount = 0
        var tagCount = 0
        var multilineStringCount = 0
        var multilineContentLines = 0
        var inMultilineBlock = false
        var multilineIndent = 0

        for (i in lines.indices) {
            val line = lines[i]
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            // Count common keys
            if (commonYamlKeys.any { trimmed.startsWith("$it:") }) {
                commonKeyCount++
            }

            // Count anchors and aliases
            if (anchorPattern.matcher(line).find()) anchorAliasCount++
            if (aliasPattern.matcher(line).find()) anchorAliasCount++

            // Count tags
            if (tagPattern.matcher(line).find()) tagCount++

            // Process multiline strings
            multilineContentLines += processMultilineString(
                line,
                trimmed,
                i,
                lines,
                inMultilineBlock,
                multilineIndent
            ).let { (newInMultiline, newIndent, contentLinesIncrement) ->
                inMultilineBlock = newInMultiline
                multilineIndent = newIndent
                contentLinesIncrement
            }

            // Check for multiline string markers
            if (multilineStringPattern.matcher(line).matches()) {
                multilineStringCount++
            }
        }

        // Check for multiline string indicators in text
        if (text.contains("|\n") || text.contains(">\n") ||
            text.contains("|+\n") || text.contains(">-\n")) {
            multilineStringCount = maxOf(multilineStringCount, 1)
        }

        return ContentFeatures(
            commonKeyCount,
            anchorAliasCount,
            tagCount,
            multilineStringCount,
            multilineContentLines
        )
    }

    // Process a line for multiline string detection
    private fun processMultilineString(
        line: String,
        trimmed: String,
        index: Int,
        lines: List<String>,
        inMultilineBlock: Boolean,
        multilineIndent: Int
    ): Triple<Boolean, Int, Int> {
        var newInMultilineBlock = inMultilineBlock
        var newMultilineIndent = multilineIndent
        var contentLinesIncrement = 0

        if (multilineStringPattern.matcher(line).matches()) {
            newInMultilineBlock = true
            newMultilineIndent = line.indexOf(':') + 1
        } else if (inMultilineBlock) {
            val indent = line.takeWhile { it.isWhitespace() }.length
            if (indent > multilineIndent && !trimmed.isEmpty()) {
                contentLinesIncrement++
            } else {
                newInMultilineBlock = false
            }
        }

        if (index > 0 && (lines[index-1].contains(": |") || lines[index-1].contains(": >")) &&
            continuationPattern.matcher(line).matches()) {
            contentLinesIncrement++
        }

        return Triple(newInMultilineBlock, newMultilineIndent, contentLinesIncrement)
    }

    // Calculate content score based on features
    private fun calculateContentScore(features: ContentFeatures): Float {
        var contentConfidence = 0.0f
        contentConfidence += (features.commonKeyCount * 0.07f).coerceAtMost(0.35f)
        contentConfidence += (features.anchorAliasCount * 0.12f).coerceAtMost(0.4f)
        contentConfidence += (features.tagCount * 0.1f).coerceAtMost(0.25f)
        contentConfidence += (features.multilineStringCount * 0.15f).coerceAtMost(0.4f)
        contentConfidence += (features.multilineContentLines * 0.03f).coerceAtMost(0.25f)

        return contentConfidence.coerceIn(0.0f, 1.0f)
    }

    // Calculate indentation confidence
    private fun calculateIndentationConfidence(lines: List<String>): Float {
        val indentationFeatures = analyzeIndentation(lines)
        return calculateIndentationScore(indentationFeatures)
    }

    // Class to hold indentation analysis results
    private data class IndentationFeatures(
        val consistentIndentation: Boolean,
        val hierarchicalIndentCount: Int,
        val indentationChanges: Int,
        val indentLevelsCount: Int
    )

    // Analyze indentation patterns
    private fun analyzeIndentation(lines: List<String>): IndentationFeatures {
        var consistentIndentation = true
        var lastIndentLevel = -1
        var indentationType: Char? = null
        var indentationChanges = 0
        var hierarchicalIndentCount = 0
        val indentLevels = mutableSetOf<Int>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

            val leadingSpaces = line.takeWhile { it.isWhitespace() }

            if (leadingSpaces.isNotEmpty()) {
                val indentStats = processIndentation(
                    leadingSpaces,
                    indentationType,
                    lastIndentLevel,
                    indentLevels
                )

                consistentIndentation = consistentIndentation && indentStats.first
                indentationType = indentStats.second

                if (lastIndentLevel != -1) {
                    val currentIndentLevel = indentStats.third
                    val indentDiff = currentIndentLevel - lastIndentLevel

                    if (indentDiff > 0) hierarchicalIndentCount++
                    if (currentIndentLevel != lastIndentLevel) indentationChanges++

                    lastIndentLevel = currentIndentLevel
                } else {
                    lastIndentLevel = indentStats.third
                }
            } else {
                lastIndentLevel = 0
                indentLevels.add(0)
            }
        }

        return IndentationFeatures(
            consistentIndentation,
            hierarchicalIndentCount,
            indentationChanges,
            indentLevels.size
        )
    }

    // Process indentation for a single line
    private fun processIndentation(
        leadingSpaces: String,
        currentType: Char?,
        lastLevel: Int,
        indentLevels: MutableSet<Int>
    ): Triple<Boolean, Char?, Int> {
        val currentIndentType = if (leadingSpaces.contains('\t')) '\t' else ' '
        val consistent = currentType == null || currentType == currentIndentType || leadingSpaces.isEmpty()

        val currentIndentLevel = if (currentIndentType == '\t') {
            leadingSpaces.count { it == '\t' }
        } else {
            leadingSpaces.length
        }

        indentLevels.add(currentIndentLevel)

        return Triple(consistent, currentIndentType, currentIndentLevel)
    }

    // Calculate indentation score
    private fun calculateIndentationScore(features: IndentationFeatures): Float {
        val baseConfidence = if (features.consistentIndentation) 0.7f else 0.3f
        val hierarchicalBoost = (features.hierarchicalIndentCount * 0.05f).coerceAtMost(0.2f)
        val indentationChangeBoost = (features.indentationChanges * 0.03f).coerceAtMost(0.2f)
        val indentLevelBoost = (features.indentLevelsCount * 0.05f).coerceAtMost(0.2f)

        return (baseConfidence + hierarchicalBoost + indentationChangeBoost + indentLevelBoost).coerceIn(0.0f, 1.0f)
    }

    // Calculate marker confidence
    private fun calculateMarkerConfidence(lines: List<String>): Float {
        val hasDocumentStart = lines.any { documentStartPattern.matcher(it).matches() }
        val hasDocumentEnd = lines.any { documentEndPattern.matcher(it).matches() }
        val documentMarkerCount = countDocumentMarkers(lines)

        return calculateMarkerScore(hasDocumentStart, hasDocumentEnd, documentMarkerCount)
    }

    // Count document markers
    private fun countDocumentMarkers(lines: List<String>): Int {
        return lines.count {
            documentStartPattern.matcher(it).matches() || documentEndPattern.matcher(it).matches()
        }
    }

    // Calculate marker score
    private fun calculateMarkerScore(hasStart: Boolean, hasEnd: Boolean, markerCount: Int): Float {
        val markerConfidence = when {
            hasStart && hasEnd -> 1.0f
            hasStart || hasEnd -> 0.8f
            else -> 0.0f
        }

        val multipleMarkerBoost = if (markerCount > 2) 0.1f else 0.0f

        return (markerConfidence + multipleMarkerBoost).coerceAtMost(1.0f)
    }

    // Apply confidence adjustments based on various factors
    private fun applyConfidenceAdjustments(baseConfidence: Float, lines: List<String>, text: String): Float {
        val sizeAdjustment = adjustForContentSize(baseConfidence, lines)
        val patternAdjustment = adjustForYamlPatterns(sizeAdjustment, lines)
        val listAdjustment = adjustForListItems(patternAdjustment, lines)
        val gitHubActionsAdjustment = adjustForGitHubActions(listAdjustment, text)
        val anchorAliasAdjustment = adjustForAnchorsAndAliases(gitHubActionsAdjustment, lines)
        val multilineAdjustment = adjustForMultilineStrings(anchorAliasAdjustment, lines)
        val suspiciousAdjustment = adjustForSuspiciousContent(multilineAdjustment, lines)

        return suspiciousAdjustment.coerceIn(0.0f, 0.95f)
    }

    // Adjust confidence based on content size
    private fun adjustForContentSize(confidence: Float, lines: List<String>): Float {
        return if (lines.size < 3 && confidence < 0.8f) {
            confidence * 0.8f
        } else {
            confidence
        }
    }

    // Adjust confidence based on YAML pattern lines
    private fun adjustForYamlPatterns(confidence: Float, lines: List<String>): Float {
        val yamlPatternLines = countYamlPatternLines(lines)

        return if (yamlPatternLines > 10 || (lines.size > 5 && yamlPatternLines.toFloat() / lines.size > 0.6f)) {
            (confidence + 0.15f).coerceAtMost(0.95f)
        } else {
            confidence
        }
    }

    // Count lines containing YAML patterns
    private fun countYamlPatternLines(lines: List<String>): Int {
        return lines.count { line ->
            keyValuePattern.matcher(line).matches() ||
                    listItemPattern.matcher(line).matches() ||
                    simpleListItemPattern.matcher(line).matches() ||
                    documentStartPattern.matcher(line).matches()
        }
    }

    // Adjust confidence based on list items
    private fun adjustForListItems(confidence: Float, lines: List<String>): Float {
        val listItemLines = lines.count {
            listItemPattern.matcher(it).matches() || simpleListItemPattern.matcher(it).matches()
        }
        val emptyLines = lines.count { it.trim().isEmpty() }

        return if (listItemLines > 5 && emptyLines > 2) {
            (confidence + 0.1f).coerceAtMost(0.9f)
        } else {
            confidence
        }
    }

    // Adjust confidence based on GitHub Actions patterns
    private fun adjustForGitHubActions(confidence: Float, text: String): Float {
        return if (text.contains("on:") && text.contains("jobs:") &&
            (text.contains("runs-on:") || text.contains("steps:"))) {
            (confidence + 0.2f).coerceAtMost(0.95f)
        } else {
            confidence
        }
    }

    // Adjust confidence based on anchors and aliases
    private fun adjustForAnchorsAndAliases(confidence: Float, lines: List<String>): Float {
        val anchorLines = lines.count { anchorPattern.matcher(it).find() }
        val aliasLines = lines.count { aliasPattern.matcher(it).find() }

        return when {
            anchorLines > 0 && aliasLines > 0 -> (confidence + 0.2f).coerceAtMost(0.95f)
            anchorLines > 0 || aliasLines > 0 -> (confidence + 0.15f).coerceAtMost(0.9f)
            else -> confidence
        }
    }

    // Adjust confidence based on multiline strings
    private fun adjustForMultilineStrings(confidence: Float, lines: List<String>): Float {
        val multilineStringLines = lines.count { multilineStringPattern.matcher(it).matches() }

        return when {
            multilineStringLines > 1 -> (confidence + 0.15f).coerceAtMost(0.95f)
            multilineStringLines > 0 -> (confidence + 0.1f).coerceAtMost(0.9f)
            else -> confidence
        }
    }

    // Adjust confidence based on suspicious content
    private fun adjustForSuspiciousContent(confidence: Float, lines: List<String>): Float {
        val suspiciousLines = countSuspiciousLines(lines)

        return if (suspiciousLines > 0) {
            val penalty = (suspiciousLines.toFloat() / lines.size * 0.3f).coerceAtMost(0.25f)
            confidence - penalty
        } else {
            confidence
        }
    }

    // Count suspicious lines that suggest non-YAML content
    private fun countSuspiciousLines(lines: List<String>): Int {
        return lines.count { line ->
            line.contains("<?") ||
                    line.contains("<!") ||
                    line.contains("<script") ||
                    line.contains("<style") ||
                    (line.contains("{") && line.contains("}") && line.contains(":\""))
        }
    }
}