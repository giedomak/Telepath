package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

interface Planner {

    fun generate(logicalPlan: LogicalPlan): PhysicalPlan

}