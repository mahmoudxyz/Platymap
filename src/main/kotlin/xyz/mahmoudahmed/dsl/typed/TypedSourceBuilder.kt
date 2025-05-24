package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.format.Format

class TypedSourceBuilder<S>(
    private val sourceClass: Class<S>
) {
    private var sourceFormat: Format = Format.JSON

    fun withFormat(format: Format): TypedSourceBuilder<S> {
        this.sourceFormat = format
        return this
    }

    fun <T> to(targetClass: Class<T>): TypedTargetBuilder<S, T> {
        return TypedTargetBuilder(sourceClass, sourceFormat, targetClass)
    }
}