package com.github.giedomak.telepathdb.costmodel

import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan
import com.github.giedomak.telepathdb.physicallibrary.IndexLookup
import com.github.giedomak.telepathdb.physicallibrary.joins.HashJoin
import com.github.giedomak.telepathdb.physicallibrary.joins.NestedLoopJoin

/**
 * This CostModel uses CPU cycles to calculate the cost of physical operators.
 */
object AdvancedCostModel : CostModel {

    /**
     * Post-order-tree-walk to calculate the cost of the root bottom-up.
     */
    override fun cost(physicalPlan: PhysicalPlan): Long {

        return when (physicalPlan.operator) {

            PhysicalPlan.LOOKUP -> IndexLookup.cost(physicalPlan.cardinality())

            PhysicalPlan.HASHJOIN -> HashJoin.cost(
                    (physicalPlan.children.first() as PhysicalPlan).cardinality(),
                    (physicalPlan.children.last() as PhysicalPlan).cardinality())

            PhysicalPlan.NESTEDLOOPJOIN -> NestedLoopJoin.cost(
                    (physicalPlan.children.first() as PhysicalPlan).cardinality(),
                    (physicalPlan.children.last() as PhysicalPlan).cardinality())

            else -> TODO("NOOOOO")
        }
    }

}