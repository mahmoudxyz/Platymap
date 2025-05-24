package xyz.mahmoudahmed.dsl.transformers

/**
 * Collection of transformers for arrays and collections.
 */
object CollectionTransformers {
    /**
     * Register all collection transformers.
     */
    fun registerAll() {
        JoinTransformer().register("join")
        JoinTransformer(", ").register("joinComma")
        JoinTransformer(" | ").register("joinPipe")
        SizeTransformer().register("size")
        FirstElementTransformer().register("first")
        LastElementTransformer().register("last")
        SortTransformer().register("sort")
        ReverseTransformer().register("reverse")
        FilterNullsTransformer().register("filterNulls")
        SplitTransformer().register("split")
        SplitTransformer(",").register("splitComma")
        MapToFieldTransformer("name").register("mapToName")
    }
}