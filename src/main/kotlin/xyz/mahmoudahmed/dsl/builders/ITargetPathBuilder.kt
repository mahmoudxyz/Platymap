package xyz.mahmoudahmed.dsl.builders

/**
 * Common interface for all target path builders.
 */
interface ITargetPathBuilder {
    /**
     * Finalizes the mapping and returns to the parent builder.
     */
    fun end(): TargetBuilder
}