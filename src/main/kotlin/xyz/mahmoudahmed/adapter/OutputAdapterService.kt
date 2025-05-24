package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.adapter.json.JsonOutputAdapter
import xyz.mahmoudahmed.adapter.xml.XmlOutputAdapter
import xyz.mahmoudahmed.format.Format
import java.io.OutputStream
import java.io.Writer

class OutputAdapterService {
    private val adapterRegistry = OutputAdapterRegistry()

    init {
        adapterRegistry.register(JsonOutputAdapter())
        adapterRegistry.register(XmlOutputAdapter())
    }

    fun serializeData(node: DataNode, format: Format): String {
        val adapter = adapterRegistry.getAdapter(format)
        return adapter.serialize(node)
    }

    fun serializeData(node: DataNode, format: Format, writer: Writer) {
        val adapter = adapterRegistry.getAdapter(format)
        adapter.serialize(node, writer)
    }

    fun serializeData(node: DataNode, format: Format, outputStream: OutputStream) {
        val adapter = adapterRegistry.getAdapter(format)
        adapter.serialize(node, outputStream)
    }

    fun registerAdapter(adapter: OutputAdapter) {
        adapterRegistry.register(adapter)
    }
}