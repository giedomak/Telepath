package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.nhaarman.mockito_kotlin.mock

class PhysicalPlanTest {

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // LogicalPlan with 1 level of children, root will get the operator param.
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