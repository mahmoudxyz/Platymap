package xyz.mahmoudahmed.dsl.transformers

import java.time.LocalDate

/**
 * Transformer to extract the month from a date.
 */
class ExtractMonthTransformer(
    inputPattern: String = "yyyy-MM-dd"
) : DateExtractionTransformer(inputPattern) {
    override fun extractFromDate(date: LocalDate): Int {
        return date.monthValue
    }
}