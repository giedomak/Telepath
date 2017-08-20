package com.github.giedomak.telepathdb.datamodels.parsetree

import com.nhaarman.mockito_kotlin.mock

class PhysicalPlanTest {

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // ParseTree with 1 level of children, root will get the operator param.
        fun generatePhysicalPlan(operator: Int, labels: List<String>): PhysicalPlan {
            val physicalPlan = PhysicalPlan(mock(), operator)

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