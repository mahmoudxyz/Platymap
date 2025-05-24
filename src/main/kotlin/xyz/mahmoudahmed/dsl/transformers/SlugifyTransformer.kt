package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode
import java.text.Normalizer
import java.util.*

/**
 * Transformer to convert text to URL-friendly slug.
 */
class SlugifyTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value).lowercase(Locale.getDefault())

        // Normalize and remove accents
        val normalized = Normalizer.normalize(str, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

        // Replace non-alphanumeric with hyphens
        val slug = normalized.replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')

        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(slug)
            is DataNode -> DataNode.StringValue(slug)
            else -> slug
        }
    }
}