package xyz.mahmoudahmed.dsl.builders

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import xyz.mahmoudahmed.dsl.transformers.TransformerRegistry
import xyz.mahmoudahmed.format.Format
import org.junit.jupiter.api.assertThrows

class MappingBuilderTest {

    private lateinit var parentBuilder: TargetBuilder
    private lateinit var singleFieldBuilder: MappingBuilder
    private lateinit var multiFieldBuilder: MappingBuilder

    @BeforeEach
    fun setup() {
        parentBuilder = TargetBuilder("source", Format.JSON, "target")
        singleFieldBuilder = MappingBuilder(parentBuilder, listOf("source.path"))
        multiFieldBuilder = MappingBuilder(parentBuilder, listOf("source.path1", "source.path2"))

        // Ensure transformer registry is initialized
        TransformerRegistry.initialize()
    }

    @Test
    fun testToWithSingleField() {
        // Execute
        val result = singleFieldBuilder.to("target.path")

        // Verify
        assert(result is SingleFieldTargetPathBuilderImpl)
    }

    @Test
    fun testToMWithMultipleFields() {
        // Execute
        val result = multiFieldBuilder.toM("target.path")

        // Verify
        assert(result is MultiFieldTargetPathBuilder)
    }

    @Test
    fun testTransform() {
        // Setup
        val transformation: (Any) -> Any = { it.toString().uppercase() }

        // Execute
        val result = singleFieldBuilder.transform(transformation)

        // Verify
        assert(result === singleFieldBuilder)

        // Verify the transformation was applied through public API
        val targetPathBuilder = result.to("target.path")
        val finalBuilder = targetPathBuilder.end()
        assert(finalBuilder === parentBuilder)
        assert(parentBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testTransformComposition() {
        // Setup
        val transformation1: (Any) -> Any = { it.toString().uppercase() }
        val transformation2: (Any) -> Any = { it.toString() + "_suffix" }

        // Execute - compose two transformations
        val result = singleFieldBuilder
            .transform(transformation1)
            .transform(transformation2)

        // Verify the transformations were composed through public API
        val targetPathBuilder = result.to("target.path")
        val finalBuilder = targetPathBuilder.end()
        assert(finalBuilder === parentBuilder)
        assert(parentBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testTransformM() {
        // Setup
        val transformation: (List<Any?>) -> Any = { list -> list.joinToString("-") }

        // Execute
        val result = multiFieldBuilder.transformM(transformation)

        // Verify
        assert(result === multiFieldBuilder)

        // Verify the transformation was applied through public API
        val targetPathBuilder = result.toM("target.path")
        val finalBuilder = targetPathBuilder.end()
        assert(finalBuilder === parentBuilder)
        assert(parentBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testTransformMWithSingleFieldThrowsException() {
        // Setup
        val transformation: (List<Any?>) -> Any = { list -> list.joinToString("-") }

        // Execute & Verify
        assertThrows<IllegalStateException> {
            singleFieldBuilder.transformM(transformation)
        }
    }

    @Test
    fun testChooseIf() {
        // Setup
        val condition: (Any) -> Boolean = { true }

        // Execute
        val result = singleFieldBuilder.chooseIf(condition)

        // Verify
        assert(result === singleFieldBuilder)

        // Verify the condition was applied through public API
        val targetPathBuilder = result.to("target.path")
        val finalBuilder = targetPathBuilder.end()
        assert(finalBuilder === parentBuilder)
        assert(parentBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testApplyTransformer() {
        // Execute
        val result = singleFieldBuilder.applyTransformer("uppercase")

        // Verify
        assert(result === singleFieldBuilder)

        // Verify the transformer was applied through public API
        val targetPathBuilder = result.to("target.path")
        val finalBuilder = targetPathBuilder.end()
        assert(finalBuilder === parentBuilder)
        assert(parentBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testApplyTransformerWithInvalidName() {
        // Execute & Verify
        assertThrows<IllegalArgumentException> {
            singleFieldBuilder.applyTransformer("non_existent_transformer")
        }
    }

    @Test
    fun testConvenienceStringTransformers() {
        // Test a sample of string transformer convenience methods
        assert(singleFieldBuilder.uppercase() === singleFieldBuilder)
        assert(singleFieldBuilder.lowercase() === singleFieldBuilder)
        assert(singleFieldBuilder.trim() === singleFieldBuilder)
        assert(singleFieldBuilder.capitalize() === singleFieldBuilder)
    }

    @Test
    fun testConvenienceNumberTransformers() {
        // Test a sample of number transformer convenience methods
        assert(singleFieldBuilder.increment() === singleFieldBuilder)
        assert(singleFieldBuilder.decrement() === singleFieldBuilder)
        assert(singleFieldBuilder.round() === singleFieldBuilder)
        assert(singleFieldBuilder.formatNumber() === singleFieldBuilder)
    }

    @Test
    fun testConvenienceDateTransformers() {
        // Test a sample of date transformer convenience methods
        assert(singleFieldBuilder.formatDateLong() === singleFieldBuilder)
        assert(singleFieldBuilder.formatDateTime() === singleFieldBuilder)
        assert(singleFieldBuilder.addOneDay() === singleFieldBuilder)
    }

    @Test
    fun testConvenienceBooleanTransformers() {
        // Test a sample of boolean transformer convenience methods
        assert(singleFieldBuilder.negate() === singleFieldBuilder)
        assert(singleFieldBuilder.booleanToString() === singleFieldBuilder)
        assert(singleFieldBuilder.toYesNo() === singleFieldBuilder)
    }

    @Test
    fun testConvenienceCollectionTransformers() {
        // Test a sample of collection transformer convenience methods
        assert(singleFieldBuilder.join() === singleFieldBuilder)
        assert(singleFieldBuilder.joinComma() === singleFieldBuilder)
        assert(singleFieldBuilder.size() === singleFieldBuilder)
    }

    @Test
    fun testConvenienceConditionalTransformers() {
        // Test a sample of conditional transformer convenience methods
        assert(singleFieldBuilder.defaultIfNull() === singleFieldBuilder)
        assert(singleFieldBuilder.ifNull() === singleFieldBuilder)
        assert(singleFieldBuilder.isNull() === singleFieldBuilder)
    }

    @Test
    fun testConvenienceTypeConversionTransformers() {
        // Test a sample of type conversion transformer convenience methods
        assert(singleFieldBuilder.stringy() === singleFieldBuilder)
        assert(singleFieldBuilder.toInt() === singleFieldBuilder)
        assert(singleFieldBuilder.toBoolean() === singleFieldBuilder)
    }

    @Test
    fun testCompositeTransformers() {
        // Test composite transformers
        assert(singleFieldBuilder.trimAndUppercase() === singleFieldBuilder)
        assert(singleFieldBuilder.trimAndLowercase() === singleFieldBuilder)
        assert(singleFieldBuilder.normalizeEmailAndMask() === singleFieldBuilder)
    }
}