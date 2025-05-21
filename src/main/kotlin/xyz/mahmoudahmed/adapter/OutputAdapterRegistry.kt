package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.FormatType

class OutputAdapterRegistry {
    private val adapters = mutableListOf<OutputAdapter>()

    fun register(adapter: OutputAdapter) {
        adapters.add(adapter)
    }

    fun getAdapter(formatType: FormatType): OutputAdapter {
        return adapters.find { it.canHandle(formatType) }
            ?: throw UnsupportedFormatException("No output adapter found for format: $formatType")
    }
}