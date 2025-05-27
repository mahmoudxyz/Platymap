package xyz.mahmoudahmed.dsl.transformers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.mahmoudahmed.adapter.DataNode

class StringTransformersTest {

    @Test
    fun `UppercaseTransformer should convert string to uppercase`() {
        val transformer = UppercaseTransformer()
        val input = DataNode.StringValue("hello world")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("HELLO WORLD", result.value)
    }

    @Test
    fun `LowercaseTransformer should convert string to lowercase`() {
        val transformer = LowercaseTransformer()
        val input = DataNode.StringValue("HELLO World")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("hello world", result.value)
    }

    @Test
    fun `TrimTransformer should remove whitespace from start and end`() {
        val transformer = TrimTransformer()
        val input = DataNode.StringValue("  hello world  ")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("hello world", result.value)
    }

    @Test
    fun `CapitalizeTransformer should capitalize first letter of each word`() {
        val transformer = CapitalizeTransformer()
        val input = DataNode.StringValue("hello world test")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("Hello World Test", result.value)
    }

    @Test
    fun `TruncateTransformer should limit string length with ellipsis`() {
        val transformer = TruncateTransformer(10)
        val input = DataNode.StringValue("This is a very long string that should be truncated")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("This is a ...", result.value)
    }

    @Test
    fun `TruncateTransformer should not modify strings shorter than max length`() {
        val transformer = TruncateTransformer(20)
        val input = DataNode.StringValue("Short string")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("Short string", result.value)
    }

    @Test
    fun `ReplaceTransformer should replace occurrences of a string`() {
        val transformer = ReplaceTransformer("world", "universe")
        val input = DataNode.StringValue("hello world")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("hello universe", result.value)
    }

    @Test
    fun `SlugifyTransformer should create URL-friendly slug`() {
        val transformer = SlugifyTransformer()
        val input = DataNode.StringValue("Hello World! Special chars: #$%^")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("hello-world-special-chars", result.value)
    }

    @Test
    fun `StripHtmlTransformer should remove HTML tags`() {
        val transformer = StripHtmlTransformer()
        val input = DataNode.StringValue("<p>Hello <b>World</b>!</p>")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("Hello World!", result.value)
    }

    @Test
    fun `MaskTransformer should mask all but last characters`() {
        val transformer = MaskTransformer(4)
        val input = DataNode.StringValue("1234567890")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("******7890", result.value)
    }

    @Test
    fun `CamelCaseTransformer should convert to camelCase`() {
        val transformer = CamelCaseTransformer()
        val input = DataNode.StringValue("Hello world test")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("helloWorldTest", result.value)
    }

    @Test
    fun `SnakeCaseTransformer should convert to snake_case`() {
        val transformer = SnakeCaseTransformer()
        val input = DataNode.StringValue("Hello World Test")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("hello_world_test", result.value)
    }

    @Test
    fun `KebabCaseTransformer should convert to kebab-case`() {
        val transformer = KebabCaseTransformer()
        val input = DataNode.StringValue("Hello World Test")
        val result = transformer.transform(input) as DataNode.StringValue

        assertEquals("hello-world-test", result.value)
    }

}
