package xyz.mahmoudahmed.dsl.transformers

import com.fasterxml.jackson.databind.ObjectMapper


/**
 * Transformer to parse a JSON string into a DataNode.
 */
class ParseJsonTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)

        return try {
            val parser = ObjectMapper()
            val parsed = parser.readValue(str, Any::class.java)
            toDataNode(parsed)
        } catch (e: Exception) {
            value
        }
    }
}