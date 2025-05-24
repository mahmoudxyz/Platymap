package xyz.mahmoudahmed.dsl.transformers

import java.util.concurrent.ConcurrentHashMap

/**
 * Registry for all available transformers.
 */
object TransformerRegistry {
    private val transformers = ConcurrentHashMap<String, ValueTransformer>()

    /**
     * Register a transformer with a unique name.
     */
    fun register(name: String, transformer: ValueTransformer) {
        transformers[name] = transformer
    }

    /**
     * Get a transformer by name.
     */
    fun get(name: String): ValueTransformer? = transformers[name]

    /**
     * Get all registered transformer names.
     */
    fun getAllNames(): Set<String> = transformers.keys

    /**
     * Apply a named transformer to a value.
     */
    fun applyTransformer(name: String, value: Any): Any {
        val transformer = transformers[name]
            ?: throw IllegalArgumentException("Transformer not found: $name")
        return transformer.transform(value)
    }

    /**
     * Initialize the registry with standard transformers.
     */
    fun initialize() {
        // Register all standard transformers
        StringTransformers.registerAll()
        NumberTransformers.registerAll()
        DateTransformers.registerAll()
        BooleanTransformers.registerAll()
        CollectionTransformers.registerAll()
        ConditionalTransformers.registerAll()
        TypeConversionTransformers.registerAll()
    }
}