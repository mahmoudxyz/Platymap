package xyz.mahmoudahmed.dsl.builders

import xyz.mahmoudahmed.dsl.core.SimpleMapping

class TargetPathBuilder(
    private val parent: TargetBuilder,
    private val sourcePath: String,
    private val targetPath: String,
    private var transformation: ((Any) -> Any)?,
    private var condition: ((Any) -> Boolean)?
) {
    fun using(function: (Any) -> Any): TargetBuilder {
        this.transformation = function
        finalizeMappingRule()
        return parent
    }

    fun chooseIf(condition: (Any) -> Boolean): TargetBuilder {
        this.condition = condition
        finalizeMappingRule()
        return parent
    }

    // Finalize and return to parent
    fun end(): TargetBuilder {
        finalizeMappingRule()
        return parent
    }

    private fun finalizeMappingRule() {
        val rule = SimpleMapping(sourcePath, targetPath, transformation, condition)
        parent.addRule(rule)
    }
}