package com.github.giedomak.telepath.datamodels.plans

import com.github.giedomak.telepath.datamodels.Query
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertNotEquals

class LogicalPlanTest {

    @Test
    fun equalsImplementation() {
        val a = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val b = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val c = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b"))
        val d = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c"))
        val e = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b", "c"))

        Assert.assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun generatesHashCode() {
        val a = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b")).hashCode()
        val b = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b")).hashCode()
        val c = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b")).hashCode()
        val d = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c")).hashCode()
        val e = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b", "c")).hashCode()

        Assert.assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun outputsToString() {
        val actual = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val expected = "LogicalPlan(operator=CONCATENATION, leaf=null, children=" + actual.children.toString() + ")"

        // then
        Assert.assertEquals(expected, actual.toString())
    }

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // LogicalPlan with 1 height of children, root will get the operator param.
        fun generateLogicalPlan(operator: Int, labels: List<String>, query: Query = mock()): LogicalPlan {
            val parseTree = LogicalPlan(query)
            parseTree.operator = operator

            // Create the children and add them to the root
            for (i in labels.indices) {
                val child = LogicalPlan(mock())
                child.setLeaf(labels[i])
                parseTree.setChild(i, child)
            }

            return parseTree
        }
    }
}
