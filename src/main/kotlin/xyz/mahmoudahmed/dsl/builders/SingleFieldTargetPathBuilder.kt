package xyz.mahmoudahmed.dsl.builders

/**
 * Interface for single field target path builder.
 */
interface SingleFieldTargetPathBuilder {
    fun end(): TargetBuilder
    fun using(function: (Any) -> Any): TargetBuilder
    fun chooseIf(condition: (Any) -> Boolean): TargetBuilder
}
