package xyz.mahmoudahmed.dsl.collections

/**
 * Builder for configuring a source path in a nested mapping.
 */
class NestedSourcePathBuilder(
    private val parent: NestedMappingBuilder,
    private val sourcePath: String
) {
    private var transformation: ((Any) -> Any)? = null

    /**
     * Specifies the target path for the mapping.
     *
     * @param targetPath Path where the value will be set
     * @return Builder for finalizing the mapping
     */
    fun to(targetPath: String): NestedTargetPathBuilder {
        return NestedTargetPathBuilder(parent, sourcePath, targetPath, transformation)
    }

    /**
     * Applies a transformation to the source value.
     *
     * @param transformation Function to transform the source value
     * @return This builder for chaining
     */
    fun transform(transformation: Any): NestedSourcePathBuilder {
        @Suppress("UNCHECKED_CAST")
        this.transformation = transformation as (Any) -> Any
        return this
    }
}
