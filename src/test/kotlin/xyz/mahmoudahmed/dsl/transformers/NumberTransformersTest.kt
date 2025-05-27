package xyz.mahmoudahmed.dsl.transformers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.mahmoudahmed.adapter.DataNode


class NumberTransformersTest {

    @Test
    fun `AddTransformer should add value to number`() {
        val transformer = AddTransformer(5)
        val input = DataNode.NumberValue(10)
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(15.0, result.value)
    }

    @Test
    fun `AddTransformer increment should add 1`() {
        val transformer = AddTransformer(1)
        val input = DataNode.NumberValue(5)
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(6.0, result.value)
    }

    @Test
    fun `AddTransformer decrement should subtract 1`() {
        val transformer = AddTransformer(-1)
        val input = DataNode.NumberValue(5)
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(4.0, result.value)
    }

    @Test
    fun `MultiplyTransformer should multiply number by factor`() {
        val transformer = MultiplyTransformer(3)
        val input = DataNode.NumberValue(4)
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(12.0, result.value)
    }

    @Test
    fun `MultiplyTransformer toPercentage should multiply by 100`() {
        val transformer = MultiplyTransformer(100)
        val input = DataNode.NumberValue(0.25)
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(25.0, result.value)
    }

    @Test
    fun `MultiplyTransformer fromPercentage should multiply by 0_01`() {
        val transformer = MultiplyTransformer(0.01)
        val input = DataNode.NumberValue(25)
        val result = transformer.transform(input) as DataNode.NumberValue

        assertEquals(0.25, result.value)
    }

    @Test
    fun `RoundTransformer should round to nearest integer`() {
        val transformer = RoundTransformer()

        val input1 = DataNode.NumberValue(4.3)
        val result1 = transformer.transform(input1) as DataNode.NumberValue
        assertEquals(4.0, result1.value)

        val input2 = DataNode.NumberValue(4.7)
        val result2 = transformer.transform(input2) as DataNode.NumberValue
        assertEquals(5.0, result2.value)

        val input3 = DataNode.NumberValue(4.5)
        val result3 = transformer.transform(input3) as DataNode.NumberValue
        assertEquals(5.0, result3.value)
    }

    @Test
    fun `FloorTransformer should round down to nearest integer`() {
        val transformer = FloorTransformer()

        val input1 = DataNode.NumberValue(4.3)
        val result1 = transformer.transform(input1) as DataNode.NumberValue
        assertEquals(4.0, result1.value)

        val input2 = DataNode.NumberValue(4.9)
        val result2 = transformer.transform(input2) as DataNode.NumberValue
        assertEquals(4.0, result2.value)

        val input3 = DataNode.NumberValue(-4.3)
        val result3 = transformer.transform(input3) as DataNode.NumberValue
        assertEquals(-5.0, result3.value)
    }

    @Test
    fun `CeilTransformer should round up to nearest integer`() {
        val transformer = CeilTransformer()

        val input1 = DataNode.NumberValue(4.1)
        val result1 = transformer.transform(input1) as DataNode.NumberValue
        assertEquals(5.0, result1.value)

        val input2 = DataNode.NumberValue(4.9)
        val result2 = transformer.transform(input2) as DataNode.NumberValue
        assertEquals(5.0, result2.value)

        val input3 = DataNode.NumberValue(-4.9)
        val result3 = transformer.transform(input3) as DataNode.NumberValue
        assertEquals(-4.0, result3.value)
    }

    @Test
    fun `AbsoluteTransformer should return absolute value`() {
        val transformer = AbsoluteTransformer()

        val input1 = DataNode.NumberValue(-5)
        val result1 = transformer.transform(input1) as DataNode.NumberValue
        assertEquals(5.0, result1.value)

        val input2 = DataNode.NumberValue(5)
        val result2 = transformer.transform(input2) as DataNode.NumberValue
        assertEquals(5.0, result2.value)

        val input3 = DataNode.NumberValue(0)
        val result3 = transformer.transform(input3) as DataNode.NumberValue
        assertEquals(0.0, result3.value)
    }

    @Test
    fun `FormatNumberTransformer should format number with pattern`() {
        val transformer = FormatNumberTransformer("#,##0.00")

        val input1 = DataNode.NumberValue(1234.56)
        val result1 = transformer.transform(input1) as DataNode.StringValue
        assertEquals("1,234.56", result1.value)

        val input2 = DataNode.NumberValue(1000)
        val result2 = transformer.transform(input2) as DataNode.StringValue
        assertEquals("1,000.00", result2.value)

        val input3 = DataNode.NumberValue(0.5)
        val result3 = transformer.transform(input3) as DataNode.StringValue
        assertEquals("0.50", result3.value)
    }

    @Test
    fun `FormatCurrencyTransformer should format number with currency symbol`() {
        val usdTransformer = FormatCurrencyTransformer("$")
        val eurTransformer = FormatCurrencyTransformer("€")
        val gbpTransformer = FormatCurrencyTransformer("£")

        val input = DataNode.NumberValue(1234.56)

        val usdResult = usdTransformer.transform(input) as DataNode.StringValue
        assertEquals("$1,234.56", usdResult.value)

        val eurResult = eurTransformer.transform(input) as DataNode.StringValue
        assertEquals("€1,234.56", eurResult.value)

        val gbpResult = gbpTransformer.transform(input) as DataNode.StringValue
        assertEquals("£1,234.56", gbpResult.value)
    }

    @Test
    fun `FormatCurrencyTransformer should handle zero and negative values`() {
        val transformer = FormatCurrencyTransformer("$")

        val input1 = DataNode.NumberValue(0)
        val result1 = transformer.transform(input1) as DataNode.StringValue
        assertEquals("$0.00", result1.value)

        val input2 = DataNode.NumberValue(-123.45)
        val result2 = transformer.transform(input2) as DataNode.StringValue
        assertEquals("-$123.45", result2.value)
    }

    @Test
    fun `RoundToDecimalPlacesTransformer should round to specified decimal places`() {
        val twoDecimalTransformer = RoundToDecimalPlacesTransformer(2)
        val wholeNumberTransformer = RoundToDecimalPlacesTransformer(0)

        val input1 = DataNode.NumberValue(3.14159)
        val result1 = twoDecimalTransformer.transform(input1) as DataNode.NumberValue
        assertEquals(3.14, result1.value)

        val input2 = DataNode.NumberValue(3.14159)
        val result2 = wholeNumberTransformer.transform(input2) as DataNode.NumberValue
        assertEquals(3.0, result2.value)

        val input3 = DataNode.NumberValue(2.995)
        val result3 = twoDecimalTransformer.transform(input3) as DataNode.NumberValue
        assertEquals(3.00, result3.value)
    }

    @Test
    fun `RoundToDecimalPlacesTransformer should handle edge cases`() {
        val transformer = RoundToDecimalPlacesTransformer(2)

        val input1 = DataNode.NumberValue(0.0)
        val result1 = transformer.transform(input1) as DataNode.NumberValue
        assertEquals(0.00, result1.value)

        val input2 = DataNode.NumberValue(-3.14159)
        val result2 = transformer.transform(input2) as DataNode.NumberValue
        assertEquals(-3.14, result2.value)
    }



    @Test
    fun `Number transformers should handle non-numeric DataNodes by extracting numeric values`() {
        val addTransformer = AddTransformer(5)

        // Assuming extractNumberValue converts "123" to 123.0, "hello" to 0.0, true to 1.0, etc.
        val numericStringInput = DataNode.StringValue("123")
        val nonNumericStringInput = DataNode.StringValue("hello")
        val booleanTrueInput = DataNode.BooleanValue(true)
        val booleanFalseInput = DataNode.BooleanValue(false)

        // Test with numeric string
        val numericStringResult = addTransformer.transform(numericStringInput) as DataNode.NumberValue
        assertEquals(128.0, numericStringResult.value) // 123 + 5

        // Test with non-numeric string (assuming it extracts 0)
        val nonNumericStringResult = addTransformer.transform(nonNumericStringInput) as DataNode.NumberValue
        assertEquals(5.0, nonNumericStringResult.value) // 0 + 5

        // Test with boolean true (assuming it extracts 1)
        val booleanTrueResult = addTransformer.transform(booleanTrueInput) as DataNode.NumberValue
        assertEquals(6.0, booleanTrueResult.value) // 1 + 5

        // Test with boolean false (assuming it extracts 0)
        val booleanFalseResult = addTransformer.transform(booleanFalseInput) as DataNode.NumberValue
        assertEquals(5.0, booleanFalseResult.value) // 0 + 5
    }


    @Test
    fun `Number transformers should handle different number types`() {
        val addTransformer = AddTransformer(1.5)

        val intInput = DataNode.NumberValue(5)
        val doubleInput = DataNode.NumberValue(5.5)
        val longInput = DataNode.NumberValue(5000000000L)

        val intResult = addTransformer.transform(intInput) as DataNode.NumberValue
        assertEquals(6.5, intResult.value)

        val doubleResult = addTransformer.transform(doubleInput) as DataNode.NumberValue
        assertEquals(7.0, doubleResult.value)

        val longResult = addTransformer.transform(longInput) as DataNode.NumberValue
        assertEquals(5000000001.5, longResult.value)
    }
}