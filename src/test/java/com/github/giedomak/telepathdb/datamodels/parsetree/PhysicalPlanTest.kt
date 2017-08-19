package com.github.giedomak.telepathdb.datamodels.parsetree

class PhysicalPlanTest {

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // ParseTree with 1 level of children, root will get the operator param.
        @JvmOverloads
        fun generatePhysicalPlan(operator: Int, labels: List<String>, isRoot: Boolean = true): PhysicalPlan {
            val physicalPlan = PhysicalPlan(isRoot, operator)

            // Create the children and add them to the root
            for (i in labels.indices) {
                val child = PhysicalPlan(false)
                child.setLeaf(labels[i])
                physicalPlan.setChild(i, child)
            }

            return physicalPlan
        }
    }
}