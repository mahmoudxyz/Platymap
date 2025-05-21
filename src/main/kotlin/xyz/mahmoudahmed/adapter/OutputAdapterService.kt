package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.adapter.json.JsonOutputAdapter
import xyz.mahmoudahmed.adapter.xml.XmlOutputAdapter
import xyz.mahmoudahmed.format.FormatType
import java.io.OutputStream
import java.io.Writer

class OutputAdapterService {
    private val adapterRegistry = OutputAdapterRegistry()

    init {
        adapterRegistry.register(JsonOutputAdapter())
        adapterRegistry.register(XmlOutputAdapter())
    }

    fun serializeData(node: DataNode, formatType: FormatType): String {
        val adapter = adapterRegistry.getAdapter(formatType)
        return adapter.serialize(node)
    }

    fun serializeData(node: DataNode, formatType: FormatType, writer: Writer) {
        val adapter = adapterRegistry.getAdapter(formatType)
        adapter.serialize(node, writer)
    }

    fun serializeData(node: DataNode, formatType: FormatType, outputStream: OutputStream) {
        val adapter = adapterRegistry.getAdapter(formatType)
        adapter.serialize(node, outputStream)
    }

    fun registerAdapter(adapter: OutputAdapter) {
        adapterRegistry.register(adapter)
    }
}