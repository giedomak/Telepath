package com.github.giedomak.telepath.planner

import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

interface Planner {

    fun generate(logicalPlan: LogicalPlan): PhysicalPlan

}