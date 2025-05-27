package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

class ConcatenateTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        if (value !is List<*>) return value
        val joined = value.filterNotNull().joinToString("")
        return DataNode.StringValue(joined)
    }
}