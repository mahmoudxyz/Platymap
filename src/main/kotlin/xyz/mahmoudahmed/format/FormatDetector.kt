package xyz.mahmoudahmed.format

/**
 * Main interface for format detection strategies.
 * Each detector should implement this interface.
 */
interface FormatDetector {
    /**
     * Returns a confidence score (0.0-1.0) indicating how likely
     * the data matches this format.
     */
    fun detect(data: ByteArray): Float

    /**
     * Returns the format type this detector is responsible for
     */
    val format: Format
}
