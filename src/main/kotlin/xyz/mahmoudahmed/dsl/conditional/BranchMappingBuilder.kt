package xyz.mahmoudahmed.dsl.conditional


/**
 * Builder for configuring a mapping within a conditional branch.
 */
class BranchMappingBuilder(
    private val parent: BranchActionBuilder,
    private val sourcePath: String
) {
    private var transformation: ((Any) -> Any)? = null

    /**
     * Specifies the target path for the mapping.
     *
     * @param targetPath Path where the value will be set
     * @return Builder for finalizing the mapping
     */
    fun to(targetPath: String): BranchTargetBuilder {
        return BranchTargetBuilder(parent, sourcePath, targetPath, transformation)
    }

    /**
     * Applies a transformation to the source value.
     *
     * @param transformation Function to transform the source value
     * @return This builder for chaining
     */
    fun transform(transformation: Any): BranchMappingBuilder {
        @Suppress("UNCHECKED_CAST")
        this.transformation = transformation as (Any) -> Any
        return this
    }
}