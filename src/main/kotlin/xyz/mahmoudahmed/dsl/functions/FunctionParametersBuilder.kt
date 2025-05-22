package xyz.mahmoudahmed.dsl.functions

/**
 * Builder for configuring function parameters.
 */
class FunctionParametersBuilder(
    val name: String,
    val parameters: List<String>
) {
    /**
     * Defines the implementation body for the function.
     *
     * @param implementation The function implementation
     * @return Builder for finalizing the function
     */
    fun body(implementation: (Array<out Any?>) -> Any?): FunctionBodyBuilder {
        return FunctionBodyBuilder(name, parameters, implementation)
    }

    /**
     * Creates a type-safe function body with a specific expected return type.
     *
     * @param implementation The function implementation with expected parameter types
     * @return Builder for finalizing the function
     */
    inline fun <reified R> typedBody(noinline implementation: (List<Any?>) -> R): FunctionBodyBuilder {
        return FunctionBodyBuilder(name, parameters) { args ->
            implementation(args.toList())
        }
    }
}