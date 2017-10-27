package com.github.giedomak.telepath.costmodel

import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

/**
 * This CostModel uses CPU cycles to calculate the cost of physical operators.
 */
object AdvancedCostModel : CostModel {

    /**
     * Post-order-tree-walk to calculate the cost of the root bottom-up.
     */
    override fun cost(physicalPlan: PhysicalPlan): Long {
        return physicalPlan.physicalOperator!!.cost()
    }
}