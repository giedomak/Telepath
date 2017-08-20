package com.github.giedomak.telepathdb.costmodel

import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.physicallibrary.operators.IndexLookup
import com.github.giedomak.telepathdb.physicallibrary.operators.HashJoin
import com.github.giedomak.telepathdb.physicallibrary.operators.NestedLoopJoin

/**
 * This CostModel uses CPU cycles to calculate the cost of physical operators.
 */
object AdvancedCostModel : CostModel {

    /**
     * Post-order-tree-walk to calculate the cost of the root bottom-up.
     */
    override fun cost(physicalPlan: PhysicalPlan): Long {
        return physicalPlan.physicalOperator.cost()
    }
}