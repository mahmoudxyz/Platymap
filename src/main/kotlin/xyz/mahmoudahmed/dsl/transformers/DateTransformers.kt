package xyz.mahmoudahmed.dsl.transformers

/**
 * Collection of date-related transformers.
 */
object DateTransformers {
    /**
     * Register all date transformers.
     */
    fun registerAll() {
        FormatDateTransformer("yyyy-MM-dd", "MMMM d, yyyy").register("formatDateLong")
        FormatDateTransformer("yyyy-MM-dd", "MM/dd/yyyy").register("formatDateShort")
        FormatDateTransformer("yyyy-MM-dd'T'HH:mm:ss'Z'", "MMM d, yyyy 'at' h:mm a").register("formatDateTime")
        DateToUnixTimestampTransformer().register("dateToTimestamp")
        UnixTimestampToDateTransformer().register("timestampToDate")
        AddDaysTransformer(1).register("addOneDay")
        AddDaysTransformer(7).register("addOneWeek")
        AddMonthsTransformer(1).register("addOneMonth")
        AddYearsTransformer(1).register("addOneYear")
        ExtractYearTransformer().register("extractYear")
        ExtractMonthTransformer().register("extractMonth")
        ExtractDayTransformer().register("extractDay")
        ToIsoDateTransformer().register("toIsoDate")
        ToIsoDateTimeTransformer().register("toIsoDateTime")
        FormatRelativeDateTransformer().register("formatRelativeDate")
    }
}