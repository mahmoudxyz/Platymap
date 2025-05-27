package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

object FieldCombinationTransformers {
    fun registerAll() {
        // Simple join transformers
        ConcatenateTransformer().register("concatenate")
        JoinWithSpaceTransformer().register("joinWithSpace")
        JoinWithCommaTransformer().register("joinWithComma")

        // Specialized transformers
        FullNameTransformer().register("fullName")
        EmailFormatterTransformer().register("emailFormat")
    }
}
