package xyz.mahmoudahmed.dsl.conditional

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.dsl.core.MappingContext
import xyz.mahmoudahmed.dsl.core.MappingRule
import xyz.mahmoudahmed.dsl.core.Platymap
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BranchMappingTest {

    private lateinit var sourceData: DataNode.ObjectNode
    private lateinit var context: MappingContext
    private lateinit var target: DataNode.ObjectNode

    // Test implementation of MappingRule
    class TestRule(private val key: String, private val value: String) : MappingRule {
        var applied = false

        override fun apply(context: MappingContext, target: Any) {
            applied = true
            if (target is DataNode.ObjectNode) {
                target.properties[key] = DataNode.StringValue(value)
            }
        }
    }

    @BeforeEach
    fun setup() {
        sourceData = DataNode.ObjectNode().apply {
            properties["type"] = DataNode.StringValue("person")
            properties["name"] = DataNode.StringValue("John")
            properties["age"] = DataNode.NumberValue(30)
        }

        context = MappingContext(sourceData)
        target = DataNode.ObjectNode()
    }

    @Test
    fun `apply should execute first matching branch`() {
        // Given
        val rule1 = TestRule("result", "branch1")
        val rule2 = TestRule("result", "branch2")

        val branch1 = ConditionalBranch({ data ->
            val node = data as DataNode.ObjectNode
            node.get("type")?.asString == "person"
        }, listOf(rule1))

        val branch2 = ConditionalBranch({ data ->
            val node = data as DataNode.ObjectNode
            node.get("type")?.asString == "company"
        }, listOf(rule2))

        val branchMapping = BranchMapping(listOf(branch1, branch2))

        // When
        branchMapping.apply(context, target)

        // Then
        assertTrue(rule1.applied)
        assertEquals(false, rule2.applied)
        assertEquals("branch1", target.get("result")?.asString)
    }

    @Test
    fun `apply should execute no branches when none match`() {
        // Given
        val rule1 = TestRule("result", "branch1")
        val rule2 = TestRule("result", "branch2")

        val branch1 = ConditionalBranch({ data ->
            val node = data as DataNode.ObjectNode
            node.get("type")?.asString == "company"
        }, listOf(rule1))

        val branch2 = ConditionalBranch({ data ->
            val node = data as DataNode.ObjectNode
            node.get("type")?.asString == "organization"
        }, listOf(rule2))

        val branchMapping = BranchMapping(listOf(branch1, branch2))

        // When
        branchMapping.apply(context, target)

        // Then
        assertEquals(false, rule1.applied)
        assertEquals(false, rule2.applied)
        assertNull(target.get("result"))
    }

    @Test
    fun `apply should execute fallback branch when it comes last`() {
        // Given
        val rule1 = TestRule("result", "branch1")
        val rule2 = TestRule("result", "fallback")

        val branch1 = ConditionalBranch({ data ->
            val node = data as DataNode.ObjectNode
            node.get("type")?.asString == "company"
        }, listOf(rule1))

        val fallbackBranch = ConditionalBranch({ true }, listOf(rule2))

        val branchMapping = BranchMapping(listOf(branch1, fallbackBranch))

        // When
        branchMapping.apply(context, target)

        // Then
        assertEquals(false, rule1.applied)
        assertTrue(rule2.applied)
        assertEquals("fallback", target.get("result")?.asString)
    }

    @Test
    fun `apply should execute multiple rules in matching branch`() {
        // Given
        val rule1 = TestRule("result1", "value1")
        val rule2 = TestRule("result2", "value2")

        val branch = ConditionalBranch({ true }, listOf(rule1, rule2))

        val branchMapping = BranchMapping(listOf(branch))

        // When
        branchMapping.apply(context, target)

        // Then
        assertTrue(rule1.applied)
        assertTrue(rule2.applied)
        assertEquals("value1", target.get("result1")?.asString)
        assertEquals("value2", target.get("result2")?.asString)
    }


}