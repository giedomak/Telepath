package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan

interface CardinalityEstimation {

    fun getCardinality(physicalPlan: PhysicalPlan): Long

}
