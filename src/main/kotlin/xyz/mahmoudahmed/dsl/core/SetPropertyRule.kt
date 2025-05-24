package xyz.mahmoudahmed.dsl.core;

import xyz.mahmoudahmed.dsl.builders.TargetBuilder


/**
 * A rule that sets a property in the mapping context.
 */
class SetPropertyRule(
        private val propertyName: String,
        private val valuePath: String
) : MappingRule {
    override fun apply(context: MappingContext, target: Any) {
        val value = context.getValueByPath(valuePath)
        context.properties[propertyName] = value ?: ""
    }
}
