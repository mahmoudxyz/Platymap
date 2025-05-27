package xyz.mahmoudahmed.dsl.conditional

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConditionalBranchTest {

    private lateinit var sourceData: DataNode.ObjectNode
    private lateinit var context: MappingContext

    // Test implementation of MappingRule
    class TestRule : MappingRule {
        var applied = false

        override fun apply(context: MappingContext, target: Any) {
            applied = true
        }
    }

    @BeforeEach
    fun setup() {
        sourceData = DataNode.ObjectNode().apply {
            properties["name"] = DataNode.StringValue("John")
            properties["age"] = DataNode.NumberValue(30)
        }

        context = MappingContext(sourceData)
    }

    @Test
    fun `evaluate should return true when condition is true`() {
        // Given
        val condition: (Any) -> Boolean = { true }
        val branch = ConditionalBranch(condition, emptyList())

        // When
        val result = branch.evaluate(sourceData)

        // Then
        assertTrue(result)
    }

    @Test
    fun `evaluate should return false when condition is false`() {
        // Given
        val condition: (Any) -> Boolean = { false }
        val branch = ConditionalBranch(condition, emptyList())

        // When
        val result = branch.evaluate(sourceData)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `actions property should return immutable list`() {
        // Given
        val rule1 = TestRule()
        val rule2 = TestRule()
        val branch = ConditionalBranch({ true }, listOf(rule1, rule2))

        // When
        val actions = branch.actions

        // Then
        assertEquals(2, actions.size)
        assertTrue(actions.contains(rule1))
        assertTrue(actions.contains(rule2))
    }

    @Test
    fun `executeActions should apply all rules`() {
        // Given
        val rule1 = TestRule()
        val rule2 = TestRule()
        val branch = ConditionalBranch({ true }, listOf(rule1, rule2))
        val target = DataNode.ObjectNode()

        // When
        branch.executeActions(context, target)

        // Then
        assertTrue(rule1.applied)
        assertTrue(rule2.applied)
    }
}