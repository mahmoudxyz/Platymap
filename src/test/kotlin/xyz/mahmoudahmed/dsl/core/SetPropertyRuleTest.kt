package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import xyz.mahmoudahmed.adapter.DataNode
import kotlin.test.assertEquals

class SetPropertyRuleTest {

    private lateinit var sourceData: DataNode.ObjectNode
    private lateinit var context: MappingContext
    private lateinit var target: DataNode.ObjectNode

    @BeforeEach
    fun setup() {
        sourceData = DataNode.ObjectNode().apply {
            properties["name"] = DataNode.StringValue("John")
            properties["age"] = DataNode.NumberValue(30)
            properties["address"] = DataNode.ObjectNode().apply {
                properties["city"] = DataNode.StringValue("New York")
            }
        }

        context = MappingContext(sourceData)
        target = DataNode.ObjectNode()
    }

    @Test
    fun `apply should set property in context from source path`() {
        // Given
        val rule = SetPropertyRule("userName", "name")

        // When
        rule.apply(context, target)

        val property = context.getProperty("userName") as DataNode.StringValue

        // Then
        assertEquals("John", property.asString)
    }

    @Test
    fun `apply should set property from nested source path`() {
        // Given
        val rule = SetPropertyRule("userCity", "address.city")

        // When
        rule.apply(context, target)

        val propertyNode = context.getProperty("userCity") as DataNode.StringValue
        // Then
        assertEquals("New York", propertyNode.asString)
    }

    @Test
    fun `apply should set empty string when source path is not found`() {
        // Given
        val rule = SetPropertyRule("nonExistentProperty", "nonExistentPath")

        // When
        rule.apply(context, target)

        // Then
        assertEquals("", context.getProperty("nonExistentProperty"))
    }

    @Test
    fun `apply should set property from literal value`() {
        // Given
        val rule = SetPropertyRule("greeting", "'Hello World'")

        // When
        rule.apply(context, target)

        // Then
        assertEquals("Hello World", context.getProperty("greeting"))
    }

    @Test
    fun `apply should set property from variable reference`() {
        // Given
        context.setVariable("myVar", "Variable Value")
        val rule = SetPropertyRule("myProp", "\$myVar")

        // When
        rule.apply(context, target)

        // Then
        assertEquals("Variable Value", context.getProperty("myProp"))
    }
}