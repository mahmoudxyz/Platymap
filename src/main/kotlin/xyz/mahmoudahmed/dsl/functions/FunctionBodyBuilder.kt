package xyz.mahmoudahmed.dsl.functions

/**
 * Builder for finalizing a function definition.
 */
class FunctionBodyBuilder(
    private val name: String,
    private val parameters: List<String>,
    private val implementation: (Array<out Any?>) -> Any?
) {
    /**
     * Builds and registers the function.
     *
     * @return The created function
     */
    fun build(): MapFunction {
        validateFunctionName()
        val function = MapFunction(name, parameters, implementation)
        FunctionRegistry.register(function)
        return function
    }

    /**
     * Builds the function without registering it.
     *
     * @return The created function
     */
    fun buildWithoutRegistering(): MapFunction {
        validateFunctionName()
        return MapFunction(name, parameters, implementation)
    }

    /**
     * Validates the function name.
     */
    private fun validateFunctionName() {
        if (name.isBlank()) {
            throw IllegalArgumentException("Function name cannot be blank")
        }

        if (!name.matches(Regex("[a-zA-Z][a-zA-Z0-9_]*"))) {
            throw IllegalArgumentException(
                "Function name must start with a letter and contain only letters, numbers, and underscores"
            )
        }
    }
}