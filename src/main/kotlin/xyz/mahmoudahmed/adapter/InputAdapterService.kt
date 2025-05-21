package xyz.mahmoudahmed.adapter


import xyz.mahmoudahmed.adapter.json.JsonInputAdapter
import xyz.mahmoudahmed.adapter.json.JsonOutputAdapter
import xyz.mahmoudahmed.adapter.xml.XmlInputAdapter
import xyz.mahmoudahmed.adapter.xml.XmlOutputAdapter
import xyz.mahmoudahmed.format.FormatDetectionService
import xyz.mahmoudahmed.format.FormatType
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.Writer

class InputAdapterService {
    private val adapterRegistry = InputAdapterRegistry()
    private val formatDetectionService = FormatDetectionService()
    private val outputAdapterRegistry = OutputAdapterRegistry()

    init {
        adapterRegistry.register(JsonInputAdapter())
        adapterRegistry.register(XmlInputAdapter())

        // Register output adapters
        outputAdapterRegistry.register(JsonOutputAdapter())
        outputAdapterRegistry.register(XmlOutputAdapter())
    }

    fun parseData(data: ByteArray): DataNode {
        val formatType = formatDetectionService.detectFormat(data)
        val adapter = adapterRegistry.getAdapter(formatType)
        return adapter.parse(data)
    }

    fun parseData(content: String): DataNode {
        val formatType = formatDetectionService.detectFormat(content.toByteArray())
        val adapter = adapterRegistry.getAdapter(formatType)
        return adapter.parse(content)
    }

    fun parseData(file: File): DataNode {
        val formatType = formatDetectionService.detectFormat(file)
        val adapter = adapterRegistry.getAdapter(formatType)
        return adapter.parse(file)
    }

    fun parseData(inputStream: InputStream): DataNode {
        val bufferedStream = if (!inputStream.markSupported()) {
            inputStream.buffered()
        } else {
            inputStream
        }

        bufferedStream.mark(8192)
        val formatType = formatDetectionService.detectFormat(bufferedStream)
        bufferedStream.reset()

        val adapter = adapterRegistry.getAdapter(formatType)
        return adapter.parse(bufferedStream)
    }

    fun registerAdapter(adapter: InputAdapter) {
        adapterRegistry.register(adapter)
    }

    fun serializeData(node: DataNode, formatType: FormatType): String {
        val adapter = outputAdapterRegistry.getAdapter(formatType)
        return adapter.serialize(node)
    }

    fun serializeData(node: DataNode, formatType: FormatType, writer: Writer) {
        val adapter = outputAdapterRegistry.getAdapter(formatType)
        adapter.serialize(node, writer)
    }

    fun serializeData(node: DataNode, formatType: FormatType, outputStream: OutputStream) {
        val adapter = outputAdapterRegistry.getAdapter(formatType)
        adapter.serialize(node, outputStream)
    }

    fun registerOutputAdapter(adapter: OutputAdapter) {
        outputAdapterRegistry.register(adapter)
    }


}