package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to round a number to a specific number of decimal places.
 */
class RoundToDecimalPlacesTransformer(private val places: Int) : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)

        val factor = Math.pow(10.0, places.toDouble())
        val rounded = Math.round(number * factor) / factor

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(rounded)
            is DataNode -> DataNode.NumberValue(rounded)
            else -> rounded
        }
    }
}