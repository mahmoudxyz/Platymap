package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.dsl.core.SimpleMapping

class SingleFieldTargetPathBuilderImpl(
    private val parent: TargetBuilder,
    private val sourcePath: String,
    private val targetPath: String,
    private var transformation: ((Any) -> Any)?,
    private var condition: ((Any) -> Boolean)?
) : SingleFieldTargetPathBuilder {

    override fun end(): TargetBuilder {
        finalizeMappingRule()
        return parent
    }

    override fun using(function: (Any) -> Any): TargetBuilder {
        this.transformation = function
        finalizeMappingRule()
        return parent
    }

    override fun chooseIf(condition: (Any) -> Boolean): TargetBuilder {
        this.condition = condition
        finalizeMappingRule()
        return parent
    }

    private fun finalizeMappingRule() {
        val rule = SimpleMapping(sourcePath, targetPath, transformation, condition)
        parent.addRule(rule)
    }
}