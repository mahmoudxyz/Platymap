package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

class FullNameTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        if (value !is List<*> || value.size < 2) return value

        val firstName = value[0]?.toString()?.trim() ?: ""
        val lastName = value[1]?.toString()?.trim() ?: ""

        // Optional middle initial
        val middleInitial = if (value.size > 2) {
            val middle = value[2]?.toString()?.trim() ?: ""
            if (middle.isEmpty()) "" else " ${middle.first()}."
        } else ""

        val fullName = "$firstName$middleInitial ${lastName}".trim()
        return DataNode.StringValue(fullName)
    }
}