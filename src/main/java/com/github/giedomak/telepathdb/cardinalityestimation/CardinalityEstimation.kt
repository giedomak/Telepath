package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

interface CardinalityEstimation {

    fun getCardinality(physicalPlan: PhysicalPlan): Long

}
