package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to format phone numbers.
 */
class FormatPhoneTransformer(
    private val countryCode: String = "",
    private val format: String = "(###) ###-####"
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        // Strip non-digit characters
        val digitsOnly = str.replace(Regex("[^0-9]"), "")

        // Apply formatting
        var formattedPhone = format
        for (digit in digitsOnly) {
            formattedPhone = formattedPhone.replaceFirst("#", digit.toString())
        }

        // Remove any remaining # characters
        formattedPhone = formattedPhone.replace("#", "")

        // Add country code if specified
        val withCode = if (countryCode.isNotEmpty() && !str.startsWith("+")) {
            "$countryCode $formattedPhone"
        } else {
            formattedPhone
        }

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(withCode)
            is DataNode -> DataNode.StringValue(withCode)
            else -> withCode
        }
    }
}