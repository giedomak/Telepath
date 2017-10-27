package com.github.giedomak.telepath.costmodel

import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

object SimpleCostModel : CostModel {

    /**
     * Our SimpleCostModel will just return the height/height of the [physicalPlan] as the cost.
     */
    override fun cost(physicalPlan: PhysicalPlan): Long {
        return physicalPlan.height().toLong()
    }
}
