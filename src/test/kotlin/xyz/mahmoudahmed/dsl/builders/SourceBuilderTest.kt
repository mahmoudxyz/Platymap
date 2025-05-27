package xyz.mahmoudahmed.dsl.builders

import org.junit.jupiter.api.Test
import xyz.mahmoudahmed.format.Format

class SourceBuilderTest {

    @Test
    fun testWithFormat() {
        // Setup
        val builder = SourceBuilder("source")

        // Execute
        val result = builder.withFormat(Format.XML)

        // Verify - since sourceFormat is private, we can only verify that a new instance was created
        assert(result !== builder)
    }

    @Test
    fun testTo() {
        // Setup
        val builder = SourceBuilder("source")

        // Execute
        val result = builder.to("target")

        // Verify
        assert(result is TargetBuilder)
        assert(result.sourceName == "source")
        assert(result.targetName == "target")
    }
}