package xyz.mahmoudahmed.dsl.transformers

import java.time.LocalDate

/**
 * Transformer to extract the day from a date.
 */
class ExtractDayTransformer(
    inputPattern: String = "yyyy-MM-dd"
) : DateExtractionTransformer(inputPattern) {
    override fun extractFromDate(date: LocalDate): Int {
        return date.dayOfMonth
    }
}
