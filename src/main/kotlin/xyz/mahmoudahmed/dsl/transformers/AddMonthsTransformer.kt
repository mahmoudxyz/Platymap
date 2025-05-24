package xyz.mahmoudahmed.dsl.transformers

import java.time.LocalDate

/**
 * Transformer to add months to a date.
 */
class AddMonthsTransformer(
    private val months: Long,
    inputPattern: String = "yyyy-MM-dd"
) : DateManipulationTransformer(inputPattern) {
    override fun manipulateDate(date: LocalDate): LocalDate {
        return date.plusMonths(months)
    }
}