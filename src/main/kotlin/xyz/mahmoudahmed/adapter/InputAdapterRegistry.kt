package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.FormatType


class InputAdapterRegistry {
    private val adapters = mutableListOf<InputAdapter>()

    fun register(adapter: InputAdapter) {
        adapters.add(adapter)
    }

    fun getAdapter(formatType: FormatType): InputAdapter {
        return adapters.find { it.canHandle(formatType) }
            ?: throw UnsupportedFormatException("No adapter found for format: $formatType")
    }
}

class UnsupportedFormatException(message: String) : RuntimeException(message)