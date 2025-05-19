package xyz.mahmoudahmed.format

import java.nio.charset.StandardCharsets

/**
 * Implementation of XML format detector
 */
class XmlFormatDetector : FormatDetector {
    override val formatType = FormatType.XML

    override fun detect(data: ByteArray): Float {
        if (data.isEmpty()) return 0.0f

        val text = String(data, StandardCharsets.UTF_8).trim()

        // Look for XML declaration or root element
        if (text.startsWith("<?xml") || text.startsWith("<")) {
            // Check for balanced tags
            val hasBothTags = text.contains("<") && text.contains(">")
            return if (hasBothTags) 0.9f else 0.5f
        }

        return 0.0f
    }
}
