package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.format.Format

class TypedTargetBuilder<S, T>(
    private val sourceClass: Class<S>,
    private val sourceFormat: Format,
    private val targetClass: Class<T>
) {
    private var targetFormat: Format = Format.JSON
    private val rules = mutableListOf<MappingRule>()

    fun withFormat(format: Format): TypedTargetBuilder<S, T> {
        this.targetFormat = format
        return this
    }

    fun <V> map(sourceAccessor: (S) -> V): TypedMappingBuilder<S, T, V> {
        return TypedMappingBuilder(this, sourceAccessor)
    }

    fun <V> forEach(collectionAccessor: (S) -> List<V>): TypedForEachBuilder<S, T, V> {
        return TypedForEachBuilder(this, collectionAccessor)
    }

    fun branch(): TypedBranchBuilder<S, T> {
        return TypedBranchBuilder(this)
    }

    // Internal method to add a rule
    internal fun addRule(rule: MappingRule) {
        rules.add(rule)
    }

    // Build method to create the final mapping
    fun build(): TypedMapping<S, T> {
        return TypedMapping(sourceClass, sourceFormat, targetClass, targetFormat, rules)
    }
}
