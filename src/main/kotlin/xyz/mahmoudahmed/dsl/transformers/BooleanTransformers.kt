package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Collection of boolean-related transformers.
 */
object BooleanTransformers {
    /**
     * Register all boolean transformers.
     */
    fun registerAll() {
        NegateTransformer().register("negate")
        BooleanToStringTransformer().register("booleanToString")
        BooleanToYesNoTransformer().register("toYesNo")
        BooleanToEnabledDisabledTransformer().register("toEnabledDisabled")
        BooleanToActiveInactiveTransformer().register("toActiveInactive")
        BooleanToNumberTransformer().register("booleanToNumber")
        StringToBooleanTransformer().register("stringToBoolean")
    }
}
