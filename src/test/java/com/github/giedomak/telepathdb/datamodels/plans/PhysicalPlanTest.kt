package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test

class PhysicalPlanTest {

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

        // LogicalPlan with 1 height of children, root will get the operator param.
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