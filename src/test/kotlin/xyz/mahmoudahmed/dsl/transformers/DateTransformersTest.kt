package xyz.mahmoudahmed.dsl.transformers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xyz.mahmoudahmed.adapter.DataNode
import java.time.LocalDate

class DateTransformersTest {

    @Test
    fun `FormatDateTransformer should convert date from one format to another`() {
        val transformer = FormatDateTransformer("yyyy-MM-dd", "MMMM d, yyyy")
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("December 25, 2023", result.value)
    }

    @Test
    fun `FormatDateTransformer formatDateLong should format to long date format`() {
        val transformer = FormatDateTransformer("yyyy-MM-dd", "MMMM d, yyyy")
        val input = DataNode.StringValue("2023-01-15")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("January 15, 2023", result.value)
    }

    @Test
    fun `FormatDateTransformer formatDateShort should format to short date format`() {
        val transformer = FormatDateTransformer("yyyy-MM-dd", "MM/dd/yyyy")
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("12/25/2023", result.value)
    }

    @Test
    fun `FormatDateTransformer formatDateTime should format ISO datetime to readable format`() {
        val transformer = FormatDateTransformer("yyyy-MM-dd'T'HH:mm:ss'Z'", "MMM d, yyyy 'at' h:mm a")
        val input = DataNode.StringValue("2023-12-25T14:30:00Z")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("Dec 25, 2023 at 2:30 PM", result.value)
    }

    @Test
    fun `DateToUnixTimestampTransformer should convert date to unix timestamp`() {
        val transformer = DateToUnixTimestampTransformer()
        val input = DataNode.StringValue("2023-01-01T00:00:00Z")
        val result = transformer.transform(input) as DataNode.NumberValue

        // January 1, 2023 00:00:00 UTC = 1672531200 seconds
        assertEquals(1672531200L, result.value.toLong())
    }

    @Test
    fun `UnixTimestampToDateTransformer should convert unix timestamp to date`() {
        val transformer = UnixTimestampToDateTransformer()
        val input = DataNode.NumberValue(1672531200L) // January 1, 2023 00:00:00 UTC
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2023-01-01T00:00:00Z", result.value)
    }

    @Test
    fun `AddDaysTransformer should add specified days to date`() {
        val transformer = AddDaysTransformer(5)
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2023-12-30", result.value)
    }

    @Test
    fun `AddDaysTransformer addOneDay should add one day`() {
        val transformer = AddDaysTransformer(1)
        val input = DataNode.StringValue("2023-12-31")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2024-01-01", result.value)
    }

    @Test
    fun `AddDaysTransformer addOneWeek should add seven days`() {
        val transformer = AddDaysTransformer(7)
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2024-01-01", result.value)
    }

    @Test
    fun `AddMonthsTransformer should add specified months to date`() {
        val transformer = AddMonthsTransformer(3)
        val input = DataNode.StringValue("2023-01-15")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2023-04-15", result.value)
    }

    @Test
    fun `AddMonthsTransformer addOneMonth should add one month`() {
        val transformer = AddMonthsTransformer(1)
        val input = DataNode.StringValue("2023-01-31")
        val result = transformer.transform(input) as DataNode.StringValue

        // February doesn't have 31 days, should adjust to last day of February
        assertEquals("2023-02-28", result.value)
    }

    @Test
    fun `AddMonthsTransformer should handle leap year`() {
        val transformer = AddMonthsTransformer(1)
        val input = DataNode.StringValue("2024-01-31") // 2024 is a leap year
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2024-02-29", result.value)
    }

    @Test
    fun `AddYearsTransformer should add specified years to date`() {
        val transformer = AddYearsTransformer(5)
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2028-12-25", result.value)
    }

    @Test
    fun `AddYearsTransformer addOneYear should add one year`() {
        val transformer = AddYearsTransformer(1)
        val input = DataNode.StringValue("2023-02-28")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2024-02-28", result.value)
    }

    @Test
    fun `ExtractYearTransformer should extract year from date`() {
        val transformer = ExtractYearTransformer()
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(2023, result.value.toInt())
    }

    @Test
    fun `ExtractMonthTransformer should extract month from date`() {
        val transformer = ExtractMonthTransformer()
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(12, result.value.toInt())
    }

    @Test
    fun `ExtractDayTransformer should extract day from date`() {
        val transformer = ExtractDayTransformer()
        val input = DataNode.StringValue("2023-12-25")
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(25, result.value.toInt())
    }

    @Test
    fun `ToIsoDateTransformer should convert date to ISO date format`() {
        val transformer = ToIsoDateTransformer()
        val input = DataNode.StringValue("12/25/2023")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2023-12-25", result.value)
    }

    @Test
    fun `ToIsoDateTimeTransformer should convert datetime to ISO datetime format`() {
        val transformer = ToIsoDateTimeTransformer()
        val input = DataNode.StringValue("Dec 25, 2023 at 2:30 PM")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("2023-12-25T14:30:00Z", result.value)
    }

    @Test
    fun `FormatRelativeDateTransformer should format date relative to now`() {
        val transformer = FormatRelativeDateTransformer()

        // Test with yesterday's date
        val yesterday = LocalDate.now().minusDays(1).toString()
        val yesterdayInput = DataNode.StringValue(yesterday)
        val yesterdayResult = transformer.transform(yesterdayInput) as DataNode.StringValue
        assertEquals("1 day ago", yesterdayResult.value)

        // Test with last week
        val lastWeek = LocalDate.now().minusDays(7).toString()
        val lastWeekInput = DataNode.StringValue(lastWeek)
        val lastWeekResult = transformer.transform(lastWeekInput) as DataNode.StringValue
        assertEquals("1 week ago", lastWeekResult.value)

        // Test with next month
        val nextMonth = LocalDate.now().plusMonths(1).toString()
        val nextMonthInput = DataNode.StringValue(nextMonth)
        val nextMonthResult = transformer.transform(nextMonthInput) as DataNode.StringValue
        assertEquals("in 1 month", nextMonthResult.value)
    }

    @Test
    fun `FormatRelativeDateTransformer should handle today and tomorrow`() {
        val transformer = FormatRelativeDateTransformer()

        // Test with today
        val today = LocalDate.now().toString()
        val todayInput = DataNode.StringValue(today)
        val todayResult = transformer.transform(todayInput) as DataNode.StringValue
        assertEquals("today", todayResult.value)

        // Test with tomorrow
        val tomorrow = LocalDate.now().plusDays(1).toString()
        val tomorrowInput = DataNode.StringValue(tomorrow)
        val tomorrowResult = transformer.transform(tomorrowInput) as DataNode.StringValue
        assertEquals("tomorrow", tomorrowResult.value)
    }

    @Test
    fun `Date transformers should handle invalid date formats gracefully`() {
        val formatTransformer = FormatDateTransformer("yyyy-MM-dd", "MMMM d, yyyy")
        val addDaysTransformer = AddDaysTransformer(1)
        val extractYearTransformer = ExtractYearTransformer()

        val invalidInput = DataNode.StringValue("not-a-date")

        // Should either return original input or throw meaningful exception
        assertThrows<Exception> {
            formatTransformer.transform(invalidInput)
        }

        assertThrows<Exception> {
            addDaysTransformer.transform(invalidInput)
        }

        assertThrows<Exception> {
            extractYearTransformer.transform(invalidInput)
        }
    }

    @Test
    fun `Date transformers should handle different input types`() {
        val timestampTransformer = UnixTimestampToDateTransformer()
        val extractYearTransformer = ExtractYearTransformer()

        // Test with DataNode.NumberValue for timestamps
        val timestampInput = DataNode.NumberValue(1672531200L)
        val timestampResult = timestampTransformer.transform(timestampInput) as DataNode.StringValue
        assertEquals("2023-01-01T00:00:00Z", timestampResult.value)

        // Test with different date string formats
        val dateInput = DataNode.StringValue("2023-12-25T10:30:00")
        val yearResult = extractYearTransformer.transform(dateInput) as DataNode.NumberValue
        assertEquals(2023, yearResult.value.toInt())
    }

    @Test
    fun `Date transformers should handle edge cases`() {
        val addDaysTransformer = AddDaysTransformer(-1) // Subtract a day
        val addMonthsTransformer = AddMonthsTransformer(-6) // Subtract 6 months

        val input = DataNode.StringValue("2023-01-01")

        val subtractDayResult = addDaysTransformer.transform(input) as DataNode.StringValue
        assertEquals("2022-12-31", subtractDayResult.value)

        val subtractMonthsResult = addMonthsTransformer.transform(input) as DataNode.StringValue
        assertEquals("2022-07-01", subtractMonthsResult.value)
    }
}