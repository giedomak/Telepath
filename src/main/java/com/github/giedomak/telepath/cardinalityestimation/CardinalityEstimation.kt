package com.github.giedomak.telepath.cardinalityestimation

import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

/**
 * Cardinality estimation interface.
 */
interface CardinalityEstimation {

    /**
     * Given a physical plan, return the cardinality.
     *
     * @param physicalPlan The physical plan for which we have to calculate the cardinality.
     * @return The cardinality of the given physical plan.
     */
    fun getCardinality(physicalPlan: PhysicalPlan): Long

}
