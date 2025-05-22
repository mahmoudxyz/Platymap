package xyz.mahmoudahmed.dsl.core

import xyz.mahmoudahmed.adapter.InputAdapterService
import xyz.mahmoudahmed.dsl.functions.FunctionBuilder
import xyz.mahmoudahmed.dsl.builders.SourceBuilder
import xyz.mahmoudahmed.dsl.typed.TypedSourceBuilder

/**
 * The main entry point for the mapping DSL.
 */
object Platymap {
    private val adapterService = InputAdapterService()

    /**
     * Starts a new mapping flow with the specified source name.
     *
     * @param source The name of the source
     * @return A builder for configuring the source
     */
    fun flow(source: String): SourceBuilder {
        return SourceBuilder(source)
    }

    /**
     * Starts a new typed mapping flow with the specified source class.
     *
     * @param sourceClass The class of the source
     * @return A builder for configuring the typed source
     */
    fun <S> flow(sourceClass: Class<S>): TypedSourceBuilder<S> {
        return TypedSourceBuilder(sourceClass)
    }

    /**
     * Creates a new function with the specified name.
     *
     * @param name The name of the function
     * @return A builder for configuring the function
     */
    fun function(name: String): FunctionBuilder {
        return FunctionBuilder(name)
    }

    /**
     * Gets the adapter service.
     *
     * @return The adapter service
     */
    fun getAdapterService(): InputAdapterService {
        return adapterService
    }
}
