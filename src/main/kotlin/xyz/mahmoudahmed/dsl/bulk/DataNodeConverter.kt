package xyz.mahmoudahmed.dsl.bulk

import xyz.mahmoudahmed.adapter.DataNode

object DataNodeConverter {
    fun convert(value: Any?): DataNode {
        return when (value) {
            null -> DataNode.NullValue
            is String -> DataNode.StringValue(value)
            is Number -> DataNode.NumberValue(value)
            is Boolean -> DataNode.BooleanValue(value)
            is DataNode -> value
            is Map<*, *> -> convertMap(value)
            is Collection<*> -> convertCollection(value)
            else -> DataNode.StringValue(value.toString())
        }
    }

    private fun convertMap(map: Map<*, *>): DataNode.ObjectNode {
        val objNode = DataNode.ObjectNode()
        map.forEach { (key, value) ->
            objNode.properties[key.toString()] = convert(value)
        }
        return objNode
    }

    private fun convertCollection(collection: Collection<*>): DataNode.ArrayNode {
        val arrayNode = DataNode.ArrayNode()
        collection.forEach { item ->
            arrayNode.elements.add(convert(item))
        }
        return arrayNode
    }
}
