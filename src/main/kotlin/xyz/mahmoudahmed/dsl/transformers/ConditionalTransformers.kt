package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Collection of conditional transformers.
 */
object ConditionalTransformers {
    /**
     * Register all conditional transformers.
     */
    fun registerAll() {
        DefaultIfNullTransformer("").register("defaultIfNull")
        DefaultIfEmptyTransformer("N/A").register("defaultIfEmpty")
        IfNullTransformer(DataNode.StringValue("null")).register("ifNull")
        IfEmptyTransformer(DataNode.StringValue("empty")).register("ifEmpty")
        IsNullTransformer().register("isNull")
        IsEmptyTransformer().register("isEmpty")
        IsNumberTransformer().register("isNumber")
        EqualsTransformer("test").register("equalsTest")
        ContainsTransformer("test").register("containsTest")
        StartsWithTransformer("prefix").register("startsWithPrefix")
        EndsWithTransformer("suffix").register("endsWithSuffix")
    }
}