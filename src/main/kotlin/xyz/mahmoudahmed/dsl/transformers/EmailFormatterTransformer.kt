package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

class EmailFormatterTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        if (value !is List<*> || value.isEmpty()) return value

        when (value.size) {
            1 -> return value[0] ?: DataNode.StringValue("")
            2 -> {
                // [username, domain]
                val username = value[0]?.toString()?.trim()?.lowercase() ?: ""
                val domain = value[1]?.toString()?.trim()?.lowercase() ?: ""
                return DataNode.StringValue("$username@$domain")
            }
            else -> {
                // [firstName, lastName, domain]
                val firstName = value[0]?.toString()?.trim()?.lowercase() ?: ""
                val lastName = value[1]?.toString()?.trim()?.lowercase() ?: ""
                val domain = value[2]?.toString()?.trim()?.lowercase() ?: ""
                return DataNode.StringValue("$firstName.$lastName@$domain")
            }
        }
    }
}