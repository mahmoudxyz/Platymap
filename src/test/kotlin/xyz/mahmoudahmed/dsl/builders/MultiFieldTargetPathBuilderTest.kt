package xyz.mahmoudahmed.dsl.builders

import org.junit.jupiter.api.Test
import xyz.mahmoudahmed.format.Format

class MultiFieldTargetPathBuilderTest {

    @Test
    fun testEndMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePaths = listOf("source.first", "source.last")
        val targetPath = "target.fullName"
        val builder = MultiFieldTargetPathBuilder(
            parentBuilder,
            sourcePaths,
            targetPath,
            null,
            null
        )

        // Execute
        val result = builder.end()

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }

    @Test
    fun testFormatMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePaths = listOf("source.first", "source.last")
        val targetPath = "target.greeting"
        val builder = MultiFieldTargetPathBuilder(
            parentBuilder,
            sourcePaths,
            targetPath,
            null,
            null
        )

        // Execute
        val result = builder.format("Hello, {0} {1}!")

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }

    @Test
    fun testWithSeparatorMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePaths = listOf("source.first", "source.last")
        val targetPath = "target.fullName"
        val builder = MultiFieldTargetPathBuilder(
            parentBuilder,
            sourcePaths,
            targetPath,
            null,
            null
        )

        // Execute
        val result = builder.withSeparator(", ")

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }

    @Test
    fun testConcatenateMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePaths = listOf("source.first", "source.last")
        val targetPath = "target.username"
        val builder = MultiFieldTargetPathBuilder(
            parentBuilder,
            sourcePaths,
            targetPath,
            null,
            null
        )

        // Execute
        val result = builder.concatenate()

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }

    @Test
    fun testJoinWithMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePaths = listOf("source.first", "source.middle", "source.last")
        val targetPath = "target.fullName"
        val builder = MultiFieldTargetPathBuilder(
            parentBuilder,
            sourcePaths,
            targetPath,
            null,
            null
        )

        // Execute
        val result = builder.joinWith(" - ")

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }
}