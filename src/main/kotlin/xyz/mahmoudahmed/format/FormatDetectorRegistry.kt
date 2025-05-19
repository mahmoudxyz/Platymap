package xyz.mahmoudahmed.format

/**
 * Registry of all available format detectors
 */
class FormatDetectorRegistry {
    private val detectors = mutableListOf<FormatDetector>()

    fun register(detector: FormatDetector) {
        detectors.add(detector)
    }

    fun detectFormat(data: ByteArray): FormatType {
        if (data.isEmpty()) return FormatType.UNKNOWN

        var bestMatch = FormatType.UNKNOWN
        var highestConfidence = 0.0f

        detectors.forEach { detector ->
            val confidence = detector.detect(data)
            if (confidence > highestConfidence) {
                highestConfidence = confidence
                bestMatch = detector.formatType
            }
        }

        return bestMatch
    }
}
