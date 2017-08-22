package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test
import java.util.stream.IntStream
import kotlin.test.assertNotEquals

class AbstractMultiTreeTest {

    @Test
    fun postOrderTraversal() {
        // given
        // We'll use the concrete implementation LogicalPlan of AbstractMultiTree.
        // Root has id 1, its three children will have ids 2, 3 and 4.
        val root = LogicalPlan(mock(), LogicalPlan.LEAF, Edge("1"))
        IntStream.range(2, 5).forEach { root.children.add(LogicalPlan(mock(), LogicalPlan.LEAF, Edge(it.toString()))) }

        // The middle child will get two more children with ids 5 and 6
        IntStream.range(5, 7).forEach { root.getChild(1)!!.children.add(LogicalPlan(mock(), LogicalPlan.LEAF, Edge(it.toString()))) }

        //          1
        //        / | \
        //       2  3  4
        //         / \
        //        5   6
        //
        // We expect the post-order tree-walk to report 2, 5, 6, 3, 4, 1
        val expected = listOf(2, 5, 6, 3, 4, 1).toString()
        val actual = root.postOrderTraversal().map { it.leaf!!.label }.toList().toString()

        // then
        Assert.assertEquals(expected, actual)
    }

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
}