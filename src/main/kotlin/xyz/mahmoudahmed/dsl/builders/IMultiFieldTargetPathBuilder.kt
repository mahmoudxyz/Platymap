package xyz.mahmoudahmed.dsl.builders

/**
 * Interface for multi-field target path builder.
 */
interface IMultiFieldTargetPathBuilder {
    fun end(): TargetBuilder
    fun format(template: String): TargetBuilder
    fun withSeparator(separator: String): TargetBuilder
    fun concatenate(): TargetBuilder
}