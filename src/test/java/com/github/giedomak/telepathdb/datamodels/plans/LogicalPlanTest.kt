package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test

class LogicalPlanTest {

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
