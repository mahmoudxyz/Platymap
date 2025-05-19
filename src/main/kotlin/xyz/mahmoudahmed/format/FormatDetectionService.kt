package xyz.mahmoudahmed.format

import java.io.File
import java.io.InputStream

/**
 * Facade class for format detection operations
 */
class FormatDetectionService(private val sampleSize: Int = 8192) {
    private val registry = FormatDetectorRegistry()

    init {
        registry.register(JsonFormatDetector())
        registry.register(XmlFormatDetector())
        registry.register(CsvFormatDetector())
        registry.register(YamlFormatDetector())
    }

    /**
     * Detect format from a byte array
     */
    fun detectFormat(data: ByteArray): FormatType {
        val sample = if (data.size > sampleSize) data.copyOfRange(0, sampleSize) else data
        return registry.detectFormat(sample)
    }

    /**
     * Detect format from an input stream
     */
    fun detectFormat(inputStream: InputStream): FormatType {
        val bytes = inputStream.readNBytes(sampleSize)
        return detectFormat(bytes)
    }

    /**
     * Detect format from a file
     */
    fun detectFormat(file: File): FormatType {
        val extensionType = detectByExtension(file.extension)
        if (extensionType != FormatType.UNKNOWN) {
            return extensionType
        }
        return file.inputStream().use { detectFormat(it) }
    }

    /**
     * Add a custom detector to the registry
     */
    fun registerDetector(detector: FormatDetector) {
        registry.register(detector)
    }

    /**
     * Helper method to detect format by file extension
     */
    private fun detectByExtension(extension: String): FormatType {
        return when (extension.lowercase()) {
            "json" -> FormatType.JSON
            "xml" -> FormatType.XML
            "csv" -> FormatType.CSV
            "yml", "yaml" -> FormatType.YAML
            "properties" -> FormatType.PROPERTIES
            "avro" -> FormatType.AVRO
            "proto" -> FormatType.PROTOBUF
            "parquet" -> FormatType.PARQUET
            "xls", "xlsx" -> FormatType.EXCEL
            else -> FormatType.UNKNOWN
        }
    }
}
