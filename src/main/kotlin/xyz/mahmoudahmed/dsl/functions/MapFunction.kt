package xyz.mahmoudahmed.dsl.functions

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Represents a mapping function that can transform data during mapping.
 *
 * @property name The function name used to reference it in mappings
 * @property parameters List of parameter names in order of expected arguments
 * @property implementation The actual function implementation that transforms data
 */
class MapFunction(
    val name: String,
    parameterNames: List<String>,
    private val implementation: (Array<out Any?>) -> Any?
) {
    private val _parameterNames = ArrayList(parameterNames)

    /**
     * Get the list of parameter names for this function.
     */
    val parameterNames: List<String>
        get() = _parameterNames.toList()

    /**
     * The number of parameters this function expects.
     */
    val parameterCount: Int
        get() = _parameterNames.size

    /**
     * Executes this function with the provided arguments.
     *
     * @param args The arguments to pass to the function
     * @return The result of the function execution
     * @throws FunctionExecutionException if the number of arguments doesn't match parameters or execution fails
     */
    fun execute(vararg args: Any?): Any? {
        validateArguments(args)

        return try {
            implementation(args)
        } catch (e: Exception) {
            throw FunctionExecutionException(
                "Error executing function '$name': ${e.message}",
                e
            )
        }
    }

    /**
     * Validates that the provided arguments match the expected parameters.
     */
    private fun validateArguments(args: Array<out Any?>) {
        if (args.size != _parameterNames.size) {
            val expectedParams = _parameterNames.joinToString(", ")
            val receivedArgs = args.joinToString(", ") { it?.toString() ?: "null" }

            throw FunctionExecutionException(
                "Function '$name' expects ${_parameterNames.size} arguments ($expectedParams), " +
                        "but got ${args.size} ($receivedArgs)"
            )
        }
    }

    /**
     * Creates a composed function that applies this function after applying the before function.
     *
     * @param before The function to apply before this function
     * @return A new function that represents the composition
     */
    fun <T> compose(before: (T) -> Any?): (T) -> Any? {
        return { input: T ->
            val intermediate = before(input)
            if (intermediate != null) {
                execute(intermediate)
            } else {
                null
            }
        }
    }

    /**
     * Creates a composed function that applies the after function to the result of this function.
     *
     * @param after The function to apply after this function
     * @return A new function that represents the composition
     */
    fun <R> andThen(after: (Any?) -> R): (Array<out Any?>) -> R {
        return { args: Array<out Any?> ->
            val result = execute(*args)
            after(result)
        }
    }
}