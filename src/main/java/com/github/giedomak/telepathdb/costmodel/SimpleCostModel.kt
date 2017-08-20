package com.github.giedomak.telepathdb.costmodel

import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

object SimpleCostModel : CostModel {

    /**
     * Our SimpleCostModel will just return the height/level of the [physicalPlan] as the cost.
     */
    override fun cost(physicalPlan: PhysicalPlan): Long {
        return physicalPlan.level().toLong()
    }
}
