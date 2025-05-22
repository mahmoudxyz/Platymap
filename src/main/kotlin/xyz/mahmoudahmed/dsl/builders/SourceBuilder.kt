package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.format.FormatType

data class SourceBuilder(
    private val sourceName: String,
    private val sourceFormat: FormatType = FormatType.JSON
) {
    fun withFormat(format: FormatType): SourceBuilder = copy(sourceFormat = format)

    fun to(target: String): TargetBuilder = TargetBuilder(sourceName, sourceFormat, target)
}
