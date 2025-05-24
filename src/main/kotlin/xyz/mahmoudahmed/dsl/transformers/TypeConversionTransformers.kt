package xyz.mahmoudahmed.dsl.transformers


/**
 * Collection of type conversion transformers.
 */
object TypeConversionTransformers {
    /**
     * Register all type conversion transformers.
     */
    fun registerAll() {
        ToStringTransformer().register("toString")
        ToIntTransformer().register("toInt")
        ToDoubleTransformer().register("toDouble")
        ToBooleanTransformer().register("toBoolean")
        ParseJsonTransformer().register("parseJson")
        ToJsonStringTransformer().register("toJsonString")
        ToBase64Transformer().register("toBase64")
        FromBase64Transformer().register("fromBase64")
        UrlEncodeTransformer().register("urlEncode")
        UrlDecodeTransformer().register("urlDecode")
    }
}