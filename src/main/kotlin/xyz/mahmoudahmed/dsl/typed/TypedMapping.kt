package xyz.mahmoudahmed.dsl.typed

import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingExecutionException
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.format.FormatType

class TypedMapping<S, T>(
    private val sourceClass: Class<S>,
    private val sourceFormat: FormatType,
    private val targetClass: Class<T>,
    private val targetFormat: FormatType,
    rules: List<MappingRule>
) {
    private val rules = ArrayList(rules)

    @Suppress("UNCHECKED_CAST")
    fun execute(source: S): T {
        try {
            val target: T = if (targetFormat == FormatType.JAVA_BEAN) {
                targetClass.getDeclaredConstructor().newInstance()
            } else {
                DataNode.ObjectNode() as T
            }

            // Create the mapping context
            val context = MappingContext(source as Any)

            // Apply all the mapping rules
            for (rule in rules) {
                rule.apply(context, target as Any)
            }

            return target
        } catch (e: Exception) {
            throw MappingExecutionException("Error executing mapping", e)
        }
    }
}