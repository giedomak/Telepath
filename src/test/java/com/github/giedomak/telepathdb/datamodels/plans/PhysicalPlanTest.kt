package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PhysicalPlanTest {

    @Test
    fun equalsImplementation() {

        val a = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf("a", "b"))
        val b = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf("a", "b"))

        val c = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.NESTED_LOOP_JOIN, listOf("a", "b"))
        val d = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf("a", "b", "c"))
        val e = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.NESTED_LOOP_JOIN, listOf("a", "b", "c"))

        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun generatesHashCode() {

        val a = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf("a", "b")).hashCode()
        val b = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf("a", "b")).hashCode()
        val c = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.NESTED_LOOP_JOIN, listOf("a", "b")).hashCode()
        val d = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf("a", "b", "c")).hashCode()
        val e = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.NESTED_LOOP_JOIN, listOf("a", "b", "c")).hashCode()

        Assert.assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun outputsToString() {
        val actual = generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"))
        val expected = "PhysicalPlan(operator=IndexLookup, leaf=null, children=" + actual.children.toString() + ")"

        // then
        Assert.assertEquals(expected, actual.toString())
    }

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // PhysicalPlan with 1 height of children, root will get the operator param.
        fun generatePhysicalPlan(operator: Int, labels: List<String>, query: Query = mock()): PhysicalPlan {
            val physicalPlan = PhysicalPlan(query, operator)

            // Create the children and add them to the root
            for (i in labels.indices) {
                val child = PhysicalPlan(mock())
                child.setLeaf(labels[i])
                physicalPlan.setChild(i, child)
            }

            return physicalPlan
        }
    }
}