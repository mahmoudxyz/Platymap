package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.format.FormatType

class TypedSourceBuilder<S>(
    private val sourceClass: Class<S>
) {
    private var sourceFormat: FormatType = FormatType.JSON

    fun withFormat(format: FormatType): TypedSourceBuilder<S> {
        this.sourceFormat = format
        return this
    }

    fun <T> to(targetClass: Class<T>): TypedTargetBuilder<S, T> {
        return TypedTargetBuilder(sourceClass, sourceFormat, targetClass)
    }
}