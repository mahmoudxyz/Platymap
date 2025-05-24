package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Collection of number-related transformers.
 */
object NumberTransformers {
    /**
     * Register all number transformers.
     */
    fun registerAll() {
        AddTransformer(1).register("increment")
        AddTransformer(-1).register("decrement")
        MultiplyTransformer(100).register("toPercentage")
        MultiplyTransformer(0.01).register("fromPercentage")
        RoundTransformer().register("round")
        FloorTransformer().register("floor")
        CeilTransformer().register("ceil")
        AbsoluteTransformer().register("abs")
        FormatNumberTransformer("#,##0.00").register("formatNumber")
        FormatCurrencyTransformer("$").register("formatUSD")
        FormatCurrencyTransformer("€").register("formatEUR")
        FormatCurrencyTransformer("£").register("formatGBP")
        RoundToDecimalPlacesTransformer(2).register("roundToTwoDecimals")
        RoundToDecimalPlacesTransformer(0).register("roundToWholeNumber")
    }
}