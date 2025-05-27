package xyz.mahmoudahmed.dsl.builders

import org.junit.jupiter.api.Test
import xyz.mahmoudahmed.format.Format

class SingleFieldTargetPathBuilderImplTest {

    @Test
    fun testEndMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePath = "source.field"
        val targetPath = "target.field"
        val builder = SingleFieldTargetPathBuilderImpl(
            parentBuilder,
            sourcePath,
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
    fun testUsingMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePath = "source.field"
        val targetPath = "target.field"
        val builder = SingleFieldTargetPathBuilderImpl(
            parentBuilder,
            sourcePath,
            targetPath,
            null,
            null
        )

        // Execute
        val transformation: (Any) -> Any = { "transformed-${it}" }
        val result = builder.using(transformation)

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }

    @Test
    fun testChooseIfMethod() {
        // Setup
        val parentBuilder = TargetBuilder("source", Format.JSON, "target")
        val sourcePath = "source.field"
        val targetPath = "target.field"
        val builder = SingleFieldTargetPathBuilderImpl(
            parentBuilder,
            sourcePath,
            targetPath,
            null,
            null
        )

        // Execute
        val condition: (Any) -> Boolean = { true }
        val result = builder.chooseIf(condition)

        // Verify
        assert(result === parentBuilder)
        val mapping = parentBuilder.build()
        assert(mapping.rules.isNotEmpty())
    }
}