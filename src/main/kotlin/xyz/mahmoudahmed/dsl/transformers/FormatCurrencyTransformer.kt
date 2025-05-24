package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.NumberFormat
import java.util.*

/**
 * Transformer to format a number as currency.
 */
class FormatCurrencyTransformer(
    private val currencySymbol: String = "$",
    private val locale: Locale = Locale.US
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)

        val formatter = NumberFormat.getCurrencyInstance(locale)
        var formatted = formatter.format(number)

        // Replace the locale's currency symbol with the specified one
        val currentSymbol = formatted.first { !it.isDigit() && !it.isWhitespace() && it != '.' && it != ',' && it != '-' }
        formatted = formatted.replace(currentSymbol.toString(), currencySymbol)

        return when (value) {
            is DataNode.NumberValue -> DataNode.StringValue(formatted)
            is DataNode -> DataNode.StringValue(formatted)
            else -> formatted
        }
    }
}