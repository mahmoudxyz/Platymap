package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.format.Format

data class SourceBuilder(
    private val sourceName: String,
    private val sourceFormat: Format = Format.JSON
) {
    fun withFormat(format: Format): SourceBuilder = copy(sourceFormat = format)

    fun to(target: String): TargetBuilder = TargetBuilder(sourceName, sourceFormat, target)
}
