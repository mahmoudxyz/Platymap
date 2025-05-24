package xyz.mahmoudahmed.format

/**
 * Registry of all available format detectors
 */
class FormatDetectorRegistry {
    private val detectors = mutableListOf<FormatDetector>()

    fun register(detector: FormatDetector) {
        detectors.add(detector)
    }

    fun detectFormat(data: ByteArray): Format {
        if (data.isEmpty()) return Format.UNKNOWN

        var bestMatch = Format.UNKNOWN
        var highestConfidence = 0.0f

        detectors.forEach { detector ->
            val confidence = detector.detect(data)
            if (confidence > highestConfidence &&  confidence > 0.2) {
                highestConfidence = confidence
                bestMatch = detector.format
            }
        }
        return bestMatch
    }
}
