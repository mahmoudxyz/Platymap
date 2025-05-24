package xyz.mahmoudahmed.dsl.transformers

import java.time.LocalDate

/**
 * Transformer to extract the year from a date.
 */
class ExtractYearTransformer(
    inputPattern: String = "yyyy-MM-dd"
) : DateExtractionTransformer(inputPattern) {
    override fun extractFromDate(date: LocalDate): Int {
        return date.year
    }
}