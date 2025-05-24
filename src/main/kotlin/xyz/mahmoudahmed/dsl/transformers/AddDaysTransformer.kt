package xyz.mahmoudahmed.dsl.transformers

import java.time.LocalDate

/**
 * Transformer to add days to a date.
 */
class AddDaysTransformer(
    private val days: Long,
    inputPattern: String = "yyyy-MM-dd"
) : DateManipulationTransformer(inputPattern) {
    override fun manipulateDate(date: LocalDate): LocalDate {
        return date.plusDays(days)
    }
}
