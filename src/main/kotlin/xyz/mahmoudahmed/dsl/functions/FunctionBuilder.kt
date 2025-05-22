package xyz.mahmoudahmed.dsl.functions

/**
 * Builder for creating a new mapping function.
 */
class FunctionBuilder(private val name: String) {
    private val parameters = mutableListOf<String>()

    /**
     * Defines the parameters for the function.
     *
     * @param params The parameter names
     * @return Builder for configuring the function parameters
     */
    fun with(vararg params: String): FunctionParametersBuilder {
        parameters.addAll(params)
        return FunctionParametersBuilder(name, parameters)
    }

    /**
     * Creates a function with no parameters.
     *
     * @return Builder for configuring the function body
     */
    fun withNoParameters(): FunctionBodyBuilder {
        return FunctionBodyBuilder(name, emptyList()) { it[0] }
    }
}

