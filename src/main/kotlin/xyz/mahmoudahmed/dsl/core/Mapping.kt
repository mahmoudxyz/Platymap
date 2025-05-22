package xyz.mahmoudahmed.dsl.core

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.format.FormatType
import java.io.File
import java.io.OutputStream
import java.io.Writer


/**
 * The main mapping class that executes the transformation from source to target.
 */
class Mapping(
    val sourceName: String,
    private val sourceFormat: FormatType,
    val targetName: String,
    private val targetFormat: FormatType,
    private val rules: List<MappingRule>
) {

    /**
     * Executes the mapping with the provided source data.
     *
     * @param sourceData The source data to map
     * @return The mapped target data
     */
    fun execute(sourceData: Any): Any {
        val adapterService = Platymap.getAdapterService()

        val sourceNode: DataNode = when (sourceData) {
            is String -> adapterService.parseData(sourceData)
            is File -> adapterService.parseData(sourceData)
            is ByteArray -> adapterService.parseData(sourceData)
            is DataNode -> sourceData
            else -> throw MappingExecutionException("Unsupported source data type: ${sourceData::class.java.name}")
        }

        val context = MappingContext(sourceNode)
        val targetNode = DataNode.ObjectNode()

        try {
            for (rule in rules) {
                rule.apply(context, targetNode)
            }
        } catch (e: Exception) {
            if (e is MappingExecutionException) {
                throw e
            } else {
                throw MappingExecutionException("Error during mapping execution", e)
            }
        }

        return targetNode
    }

    /**
     * Executes the mapping and returns the result in the specified format.
     *
     * @param sourceData The source data to map
     * @param outputFormat The desired output format
     * @return The mapped data as a string in the specified format
     */
    fun executeToFormat(sourceData: Any, outputFormat: FormatType): String {
        val result = execute(sourceData) as DataNode
        return Platymap.getAdapterService().serializeData(
            result,
            outputFormat
        )
    }

    /**
     * Executes the mapping and writes the result in the specified format to a writer.
     *
     * @param sourceData The source data to map
     * @param outputFormat The desired output format
     * @param writer The writer to write the result to
     */
    fun executeToFormat(sourceData: Any, outputFormat: FormatType, writer: Writer) {
        val result = execute(sourceData) as DataNode
        Platymap.getAdapterService().serializeData(
            result,
            outputFormat,
            writer
        )
    }

    /**
     * Executes the mapping and writes the result in the specified format to an output stream.
     *
     * @param sourceData The source data to map
     * @param outputFormat The desired output format
     * @param outputStream The output stream to write the result to
     */
    fun executeToFormat(sourceData: Any, outputFormat: FormatType, outputStream: OutputStream) {
        val result = execute(sourceData) as DataNode
        Platymap.getAdapterService().serializeData(
            result,
            outputFormat,
            outputStream
        )
    }

    /**
     * Executes the mapping and returns the result as a JSON string.
     *
     * @param sourceData The source data to map
     * @return The mapped data as a JSON string
     */
    fun executeToJson(sourceData: Any): String {
        return executeToFormat(sourceData, FormatType.JSON)
    }

    /**
     * Executes the mapping and returns the result as an XML string.
     *
     * @param sourceData The source data to map
     * @return The mapped data as an XML string
     */
    fun executeToXml(sourceData: Any): String {
        return executeToFormat(sourceData, FormatType.XML)
    }
}
