package xyz.mahmoudahmed.adapter

sealed class DataNode {
    data class ObjectNode(val properties: MutableMap<String, DataNode> = mutableMapOf()) : DataNode() {
        operator fun get(key: String): DataNode? = properties[key]
        operator fun set(key: String, value: DataNode) {
            properties[key] = value
        }
    }

    data class ArrayNode(val elements: MutableList<DataNode> = mutableListOf()) : DataNode() {
        operator fun get(index: Int): DataNode? =
            if (index < elements.size) elements[index] else null

        fun add(element: DataNode) = elements.add(element)
    }

    data class StringValue(val value: String) : DataNode()
    data class NumberValue(val value: Number) : DataNode()
    data class BooleanValue(val value: Boolean) : DataNode()
    data object NullValue : DataNode()


    val asString: String? get() = (this as? StringValue)?.value
    val asInt: Int? get() = (this as? NumberValue)?.value?.toInt()
    val asDouble: Double? get() = (this as? NumberValue)?.value?.toDouble()
    val asBoolean: Boolean? get() = (this as? BooleanValue)?.value
    val asObject: ObjectNode? get() = this as? ObjectNode
    val asArray: ArrayNode? get() = this as? ArrayNode
    val isNull: Boolean get() = this is NullValue
}
