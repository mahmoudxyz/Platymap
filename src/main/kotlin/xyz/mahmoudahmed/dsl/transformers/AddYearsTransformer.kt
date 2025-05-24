package xyz.mahmoudahmed.dsl.transformers

import java.time.LocalDate

/**
 * Transformer to add years to a date.
 */
class AddYearsTransformer(
    private val years: Long,
    inputPattern: String = "yyyy-MM-dd"
) : DateManipulationTransformer(inputPattern) {
    override fun manipulateDate(date: LocalDate): LocalDate {
        return date.plusYears(years)
    }
}
