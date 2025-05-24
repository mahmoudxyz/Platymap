package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets
import java.util.regex.Pattern


/**
 * XML format detector with thorough validation
 * and intelligent confidence scoring.
 */
class XmlFormatDetector : FormatDetector {
    override val format = Format.XML

    // Regex patterns for XML structure analysis
    private val XML_DECLARATION_PATTERN = Pattern.compile(
        """^\s*<\?xml\s+version\s*=\s*["'][\d.]+["']"""
    )
    private val XML_COMMENT_PATTERN = Pattern.compile("""<!--.*?-->""", Pattern.DOTALL)
    private val XML_TAG_PATTERN = Pattern.compile("""<([^!?][^>\s]*)[^>]*>""")
    private val XML_CLOSED_TAG_PATTERN = Pattern.compile("""<([^!?][^>\s]*)[^>]*/>""")
    private val XML_NAMESPACE_PATTERN = Pattern.compile("""xmlns(?::[a-zA-Z0-9-_]+)?\s*=\s*["'][^"']*["']""")
    private val XML_CDATA_PATTERN = Pattern.compile("""<!\[CDATA\[.*?]]>""", Pattern.DOTALL)

    private val MAX_SAMPLE_SIZE = 16 * 1024

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        val sampleData = if (data.size > MAX_SAMPLE_SIZE)
            data.copyOfRange(0, MAX_SAMPLE_SIZE) else data

        val text = String(sampleData, StandardCharsets.UTF_8).trim()

        if (!text.contains('<') || !text.contains('>')) {
            return 0.0f
        }

        val confidence = analyzeSyntax(text)

        return confidence
    }

    /**
     * Analyzes XML syntax characteristics to determine confidence.
     */
    private fun analyzeSyntax(text: String): Float {
        var score = 0.0f

        val hasDeclaration = XML_DECLARATION_PATTERN.matcher(text).find()
        if (hasDeclaration) {
            score += 0.2f
        }

        val withoutComments = XML_COMMENT_PATTERN.matcher(text).replaceAll("")

        val tagMatcher = XML_TAG_PATTERN.matcher(withoutComments)
        if (!tagMatcher.find()) {
            return score * 0.5f
        }

        var tagCount = 0
        tagMatcher.reset()
        val tagNames = mutableSetOf<String>()
        while (tagMatcher.find()) {
            tagCount++
            tagNames.add(tagMatcher.group(1))
        }

        val closedTagMatcher = XML_CLOSED_TAG_PATTERN.matcher(withoutComments)
        var selfClosingCount = 0
        while (closedTagMatcher.find()) {
            selfClosingCount++
        }

        var balanced = true
        for (tagName in tagNames) {
            val openPattern = Pattern.compile("<$tagName(?:\\s+[^>]*)?[^/]>")
            val closePattern = Pattern.compile("</$tagName>")

            val openCount = countMatches(withoutComments, openPattern)
            val closeCount = countMatches(withoutComments, closePattern)

            if (openCount != closeCount) {
                balanced = false
                break
            }
        }

        val hasNamespaces = XML_NAMESPACE_PATTERN.matcher(withoutComments).find()

        val hasCData = XML_CDATA_PATTERN.matcher(withoutComments).find()

        score += 0.4f

        if (tagCount > 1) score += 0.1f
        if (tagCount > 5) score += 0.1f
        if (selfClosingCount > 0) score += 0.1f
        if (balanced) score += 0.3f
        if (hasNamespaces) score += 0.1f
        if (hasCData) score += 0.1f

        return score.coerceIn(0.0f, 1.0f)
    }


    /**
     * Counts matches of a pattern in text.
     */
    private fun countMatches(text: String, pattern: Pattern): Int {
        val matcher = pattern.matcher(text)
        var count = 0
        while (matcher.find()) {
            count++
        }
        return count
    }
}