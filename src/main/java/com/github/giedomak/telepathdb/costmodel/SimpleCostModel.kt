package com.github.giedomak.telepathdb.costmodel

import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan

object SimpleCostModel : CostModel {

    /**
     * Our SimpleCostModel will just return the height/level op the [physicalPlan] as the cost.
     */
    override fun cost(physicalPlan: PhysicalPlan): Long {
        return physicalPlan.level().toLong()
    }
}
