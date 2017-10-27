package com.github.giedomak.telepath.planner.enumerator

import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

/**
 * Merge two given physical plans by enumerating the applicable physical operators.
 */
interface Enumerator {

    fun enumerate(tree1: PhysicalPlan, tree2: PhysicalPlan, logicalOperator: Int): Sequence<PhysicalPlan>

}