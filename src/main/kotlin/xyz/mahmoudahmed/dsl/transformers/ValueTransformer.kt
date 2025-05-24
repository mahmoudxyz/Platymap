package xyz.mahmoudahmed.dsl.transformers

/**
 * Base interface for all value transformers.
 */
interface ValueTransformer {
    /**
     * Transform a value.
     */
    fun transform(value: Any): Any

    /**
     * Register this transformer with the registry.
     */
    fun register(name: String) {
        TransformerRegistry.register(name, this)
    }
}