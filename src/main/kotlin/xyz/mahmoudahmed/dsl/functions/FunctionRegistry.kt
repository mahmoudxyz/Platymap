package xyz.mahmoudahmed.dsl.functions

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Registry for mapping functions.
 */
object FunctionRegistry {
    private val functions = ConcurrentHashMap<String, MapFunction>()
    private val typeConverters = ConcurrentHashMap<Pair<KClass<*>, KClass<*>>, (Any) -> Any>()

    /**
     * Registers a function in the registry.
     *
     * @param function The function to register
     * @throws IllegalArgumentException if a function with the same name already exists
     */
    fun register(function: MapFunction) {
        val existing = functions.putIfAbsent(function.name, function)
        if (existing != null) {
            throw IllegalArgumentException("Function already registered: ${function.name}")
        }
    }

    /**
     * Gets a function from the registry by name.
     *
     * @param name The function name
     * @return The function
     * @throws FunctionNotFoundException if the function is not found
     */
    fun get(name: String): MapFunction {
        return functions[name] ?: throw FunctionNotFoundException(name)
    }

    /**
     * Calls a function by name with the provided arguments.
     *
     * @param name The function name
     * @param args The arguments to pass to the function
     * @return The result of the function call
     * @throws FunctionNotFoundException if the function is not found
     * @throws FunctionExecutionException if the function execution fails
     */
    fun call(name: String, vararg args: Any?): Any? {
        return get(name).execute(*args)
    }

    /**
     * Registers a type converter function.
     *
     * @param sourceType The source type
     * @param targetType The target type
     * @param converter The converter function
     */
    fun <S : Any, T : Any> registerTypeConverter(
        sourceType: KClass<S>,
        targetType: KClass<T>,
        converter: (S) -> T
    ) {
        @Suppress("UNCHECKED_CAST")
        typeConverters[Pair(sourceType, targetType)] = converter as (Any) -> Any
    }

    /**
     * Converts a value from one type to another if a converter is registered.
     *
     * @param value The value to convert
     * @param targetType The target type
     * @return The converted value
     * @throws IllegalArgumentException if no converter is found
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> convertType(value: Any, targetType: KClass<T>): T {
        if (targetType.isInstance(value)) {
            return value as T
        }

        val sourceType = value::class
        val converter = typeConverters[Pair(sourceType, targetType)]
            ?: throw IllegalArgumentException("No converter found from ${sourceType.simpleName} to ${targetType.simpleName}")

        return converter(value) as T
    }

    /**
     * Checks if a function exists in the registry.
     *
     * @param name The function name
     * @return true if the function exists, false otherwise
     */
    fun exists(name: String): Boolean {
        return functions.containsKey(name)
    }

    /**
     * Unregisters a function from the registry.
     *
     * @param name The function name
     * @return true if the function was unregistered, false otherwise
     */
    fun unregister(name: String): Boolean {
        return functions.remove(name) != null
    }

    /**
     * Clears all functions from the registry.
     */
    fun clear() {
        functions.clear()
    }

    /**
     * Gets all registered function names.
     *
     * @return Set of function names
     */
    fun getAllFunctionNames(): Set<String> {
        return functions.keys.toSet()
    }

    /**
     * Initialize the registry with built-in functions.
     */
    init {
        // String manipulation functions
        register(MapFunction("trim", listOf("value")) { args ->
            val value = args[0]
            when (value) {
                is String -> value.trim()
                else -> value?.toString()?.trim()
            }
        })

        register(MapFunction("concat", listOf("value1", "value2")) { args ->
            args[0].toString() + args[1].toString()
        })

        register(MapFunction("upperCase", listOf("value")) { args ->
            val value = args[0]
            when (value) {
                is String -> value.uppercase()
                else -> value?.toString()?.uppercase()
            }
        })

        register(MapFunction("lowerCase", listOf("value")) { args ->
            val value = args[0]
            when (value) {
                is String -> value.lowercase()
                else -> value?.toString()?.lowercase()
            }
        })

        // Numeric functions
        register(MapFunction("add", listOf("a", "b")) { args ->
            val a = args[0]
            val b = args[1]
            when {
                a is Number && b is Number -> a.toDouble() + b.toDouble()
                else -> throw FunctionExecutionException(
                    "add function requires numeric arguments, got ${a?.javaClass?.name} and ${b?.javaClass?.name}"
                )
            }
        })

        register(MapFunction("subtract", listOf("a", "b")) { args ->
            val a = args[0]
            val b = args[1]
            when {
                a is Number && b is Number -> a.toDouble() - b.toDouble()
                else -> throw FunctionExecutionException(
                    "subtract function requires numeric arguments, got ${a?.javaClass?.name} and ${b?.javaClass?.name}"
                )
            }
        })

        // Type conversion functions
        register(MapFunction("toString", listOf("value")) { args ->
            args[0]?.toString() ?: ""
        })

        register(MapFunction("toNumber", listOf("value")) { args ->
            val value = args[0]?.toString() ?: return@MapFunction null
            try {
                value.toDouble()
            } catch (e: NumberFormatException) {
                throw FunctionExecutionException("Cannot convert '$value' to a number")
            }
        })

        register(MapFunction("toBoolean", listOf("value")) { args ->
            val value = args[0]?.toString()?.lowercase() ?: return@MapFunction false
            when (value) {
                "true", "yes", "1", "on" -> true
                "false", "no", "0", "off" -> false
                else -> throw FunctionExecutionException("Cannot convert '$value' to a boolean")
            }
        })

        // Collection functions
        register(MapFunction("first", listOf("collection")) { args ->
            when (val collection = args[0]) {
                is Collection<*> -> collection.firstOrNull()
                is Array<*> -> collection.firstOrNull()
                else -> throw FunctionExecutionException(
                    "first function requires a collection, got ${collection?.javaClass?.name}"
                )
            }
        })

        register(MapFunction("size", listOf("collection")) { args ->
            when (val collection = args[0]) {
                is Collection<*> -> collection.size
                is Array<*> -> collection.size
                is String -> collection.length
                is Map<*, *> -> collection.size
                else -> throw FunctionExecutionException(
                    "size function requires a collection, string, or map, got ${collection?.javaClass?.name}"
                )
            }
        })

        // Conditional functions
        register(MapFunction("ifNull", listOf("value", "defaultValue")) { args ->
            args[0] ?: args[1]
        })

        register(MapFunction("ifEmpty", listOf("value", "defaultValue")) { args ->
            val value = args[0]
            when {
                value == null -> args[1]
                value is String && value.isEmpty() -> args[1]
                value is Collection<*> && value.isEmpty() -> args[1]
                value is Map<*, *> && value.isEmpty() -> args[1]
                value is Array<*> && value.isEmpty() -> args[1]
                else -> value
            }
        })
    }
}