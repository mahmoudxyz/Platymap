package xyz.mahmoudahmed.dsl.builders

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import xyz.mahmoudahmed.format.Format
import xyz.mahmoudahmed.dsl.bulk.BulkMappingBuilder
import xyz.mahmoudahmed.dsl.collections.ForEachBuilder
import xyz.mahmoudahmed.dsl.conditional.BranchBuilder
import xyz.mahmoudahmed.dsl.structure.NestingBuilder
import xyz.mahmoudahmed.logging.LogLevel
import xyz.mahmoudahmed.logging.LoggingConfig.minLevel
import java.lang.System.console

class TargetBuilderTest {

    private lateinit var targetBuilder: TargetBuilder

    @BeforeEach
    fun setup() {
        targetBuilder = TargetBuilder("source", Format.JSON, "target")
    }

    @Test
    fun testWithFormat() {
        // Execute
        val result = targetBuilder.withFormat(Format.XML)

        // Verify - since targetFormat is private, we can only verify that a new instance was created
        assert(result !== targetBuilder)
    }

    @Test
    fun testMapWithSinglePath() {
        // Execute
        val result = targetBuilder.map("source.path")

        // Verify
        assert(result is MappingBuilder)
    }

    @Test
    fun testMapWithMultiplePaths() {
        // Execute
        val result = targetBuilder.map("source.path1", "source.path2")

        // Verify
        assert(result is MappingBuilder)
    }

    @Test
    fun testMapAll() {
        // Execute
        val result = targetBuilder.mapAll("source.*")

        // Verify
        assert(result is BulkMappingBuilder)
    }

    @Test
    fun testMapAllExcept() {
        // Execute
        val result = targetBuilder.mapAllExcept("source.exclude1", "source.exclude2")

        // Verify
        assert(result is BulkMappingBuilder)
    }

    @Test
    fun testNest() {
        // Execute
        val result = targetBuilder.nest("source.nested.*")

        // Verify
        assert(result is NestingBuilder)
    }

    @Test
    fun testFlatten() {
        // Execute
        val result = targetBuilder.flatten("source.nested")

        // Verify
        assert(result is BulkMappingBuilder)
    }

    @Test
    fun testForEach() {
        // Execute
        val result = targetBuilder.forEach("source.items")

        // Verify
        assert(result is ForEachBuilder)
    }

    @Test
    fun testBranch() {
        // Execute
        val result = targetBuilder.branch()

        // Verify
        assert(result is BranchBuilder)
    }

    @Test
    fun testBuild() {
        // Execute
        val mapping = targetBuilder.build()

        // Verify
        assert(mapping.sourceName == "source")
        assert(mapping.targetName == "target")
    }

    @Test
    fun testAddRule() {
        // Setup - create a simple mapping rule through public API
        targetBuilder.map("source.field").to("target.field").end()

        // Execute
        val mapping = targetBuilder.build()

        // Verify
        assert(mapping.rules.isNotEmpty())
    }

    @Test
    fun testLogging() {
        // Execute
        val result = targetBuilder.log("Test message")

        // Verify
        assert(result === targetBuilder)
        assert(targetBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testLogLevelConvenienceMethods() {
        // Test the various log level convenience methods
        assert(targetBuilder.trace("Trace message") === targetBuilder)
        assert(targetBuilder.debug("Debug message") === targetBuilder)
        assert(targetBuilder.info("Info message") === targetBuilder)
        assert(targetBuilder.warn("Warning message") === targetBuilder)
        assert(targetBuilder.error("Error message") === targetBuilder)
    }

    @Test
    fun testLogData() {
        // Execute
        val result = targetBuilder.logData("Log message", "path1", "path2", level = LogLevel.INFO)

        // Verify
        assert(result === targetBuilder)
        assert(targetBuilder.build().rules.isNotEmpty())
    }

    @Test
    fun testSetProperty() {
        // Execute
        val result = targetBuilder.setProperty("propertyName", "source.value")

        // Verify
        assert(result === targetBuilder)
        assert(targetBuilder.build().rules.isNotEmpty())
    }


}