package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.Format

class OutputAdapterRegistry {
    private val adapters = mutableListOf<OutputAdapter>()

    fun register(adapter: OutputAdapter) {
        adapters.add(adapter)
    }

    fun getAdapter(format: Format): OutputAdapter {
        return adapters.find { it.canHandle(format) }
            ?: throw UnsupportedFormatException("No output adapter found for format: $format")
    }
}