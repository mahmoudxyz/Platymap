package xyz.mahmoudahmed.dsl.builders

class MappingBuilder(
    private val parent: TargetBuilder,
    private val sourcePath: String
) {
    private var transformation: ((Any) -> Any)? = null
    private var condition: ((Any) -> Boolean)? = null

    fun to(targetPath: String): TargetPathBuilder {
        return TargetPathBuilder(parent, sourcePath, targetPath, transformation, condition)
    }

    fun transform(transformation: (Any) -> Any): MappingBuilder {
        this.transformation = transformation
        return this
    }

    fun chooseIf(condition: (Any) -> Boolean): MappingBuilder {
        this.condition = condition
        return this
    }

    // Extension point for custom transformations
    fun uppercase(): MappingBuilder {
        return transform { value -> value.toString().uppercase() }
    }

    fun lowercase(): MappingBuilder {
        return transform { value -> value.toString().lowercase() }
    }
}