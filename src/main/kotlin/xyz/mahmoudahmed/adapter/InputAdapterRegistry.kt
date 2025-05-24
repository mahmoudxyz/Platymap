package xyz.mahmoudahmed.adapter

import xyz.mahmoudahmed.format.Format


class InputAdapterRegistry {
    private val adapters = mutableListOf<InputAdapter>()

    fun register(adapter: InputAdapter) {
        adapters.add(adapter)
    }

    fun getAdapter(format: Format): InputAdapter {
        return adapters.find { it.canHandle(format) }
            ?: throw UnsupportedFormatException("No adapter found for format: $format")
    }
}

class UnsupportedFormatException(message: String) : RuntimeException(message)