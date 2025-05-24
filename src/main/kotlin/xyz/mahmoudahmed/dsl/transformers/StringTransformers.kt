package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern

/**
 * Collection of string-related transformers.
 */
object StringTransformers {
    /**
     * Register all string transformers.
     */
    fun registerAll() {
        UppercaseTransformer().register("uppercase")
        LowercaseTransformer().register("lowercase")
        TrimTransformer().register("trim")
        CapitalizeTransformer().register("capitalize")
        TruncateTransformer(30).register("truncate")
        TruncateTransformer(100, "...").register("truncateLong")
        ReplaceTransformer(" ", "-").register("spacesToDashes")
        ReplaceTransformer("\n", "<br>").register("newlinesToBreaks")
        SlugifyTransformer().register("slugify")
        StripHtmlTransformer().register("stripHtml")
        PadLeftTransformer(10, '0').register("padLeft10")
        PadRightTransformer(20, ' ').register("padRight20")
        MaskTransformer(4).register("mask")
        TitleCaseTransformer().register("titleCase")
        CamelCaseTransformer().register("camelCase")
        SnakeCaseTransformer().register("snakeCase")
        KebabCaseTransformer().register("kebabCase")
        NormalizeEmailTransformer().register("normalizeEmail")
    }
}