package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.stream.IntStream
import kotlin.test.assertNotEquals

class LogicalPlanTest {

    @Test
    fun postOrderTraversal() {
        // given
        // Root has id 1, its three children will have ids 2, 3 and 4.
        val root = LogicalPlan(mock())
        root.setLeaf("1")
        IntStream.range(2, 5).forEach { root.children.add(LogicalPlan(mock(), LogicalPlan.LEAF, Edge(it.toString()))) }

        // The middle child will get two more childs with ids 5 and 6
        IntStream.range(5, 7).forEach { root.getChild(1)!!.children.add(LogicalPlan(mock(), LogicalPlan.LEAF, Edge(it.toString()))) }

        //          1
        //        / | \
        //       2  3  4
        //         / \
        //        5   6

        // We expect the post-order tree-walk to report 2, 5, 6, 3, 4, 1
        val expected = listOf(2, 5, 6, 3, 4, 1).toString()
        val actual = root.postOrderTraversal().map { it.leaf!!.label }.toList().toString()

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun equalsImplementation() {
        val a = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val b = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val c = generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b"))
        val d = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c"))
        val e = generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b", "c"))

        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun generatesHashCode() {
        val a = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b")).hashCode()
        val b = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b")).hashCode()
        val c = generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b")).hashCode()
        val d = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c")).hashCode()
        val e = generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b", "c")).hashCode()

        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun outputsToString() {
        val actual = generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val expected = "LogicalPlan(operator=CONCATENATION, leaf=null, children=" + actual.children.toString() + ")"

        // then
        assertEquals(expected, actual.toString())
    }

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // LogicalPlan with 1 level of children, root will get the operator param.
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
