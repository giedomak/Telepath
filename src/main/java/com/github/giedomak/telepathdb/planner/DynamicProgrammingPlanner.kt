/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.utilities.Logger

/**
 * The DynamicProgrammingPlanner uses the DPsize algorithm for inspiration.
 *
 * We generate the best physical plan for smaller subtrees of a given logical plan. Which will guarantee we
 * will have the cheapest physical plan for the full logical plan.
 */
object DynamicProgrammingPlanner : Planner {

    /**
     * Generate the cheapest physical plan for a given logical plan.
     *
     * @param logicalPlan The logical plan for which we have to generate the cheapest physical plan.
     * @return The cheapest physical plan for the given logical plan.
     */
    override fun generate(logicalPlan: LogicalPlan): PhysicalPlan {

        val enumerator = logicalPlan.query.telepathDB.enumerator

        // Make sure we are dealing with a flattened logical plan.
        logicalPlan.flatten()

        Logger.debug("Flattened logical plan:")
        logicalPlan.print()

        // We have to iterate until we reach the size of the logicalPlan we are searching a physical plan for.
        val n = logicalPlan.getSize()

        val cheapestPhysicalPlans = hashMapOf<Int, PhysicalPlan>()

        // Init the bestPlan for all subtrees of size 1 to:
        //    INDEX_LOOKUP
        //         |
        //        LEAF
        logicalPlan.subtreesOfSize(1).forEach {
            cheapestPhysicalPlans.put(
                    it.hashCode(),
                    PhysicalPlan(logicalPlan.query, it.leaf!!))
        }

        // Alright, so we are increasingly calculating the best physical plan for each subtree of a given size.
        for (size in 2..n) {

            // Our sliding window will split the subtree size into a left-part, and a right-part.
            for (leftSize in 1..(size - 1)) {

                val rightSize = size - leftSize

                // Iterate over all subtrees of these left- and right-sizes.
                for (subtree1 in logicalPlan.subtreesOfSize(leftSize)) {

                    for (subtree2 in logicalPlan.subtreesOfSize(rightSize)) {

                        // Check if subtree1 and subtree2 are contained in our logicalPlan through any operator.
                        for (operator in LogicalPlan.OPERATORS) {

                            // Actually check for containment with the operator.
                            if (logicalPlan.containsSubtreesThroughOperator(subtree1, subtree2, operator)) {

                                // YAY, we are contained, so we have to calculate the best physical plan.
                                // Re-use the best physical plan already calculated for these left- and right-parts.
                                val plan1 = cheapestPhysicalPlans.getValue(subtree1.hashCode())
                                val plan2 = cheapestPhysicalPlans.getValue(subtree2.hashCode())

                                // Enumerate or logical operator into applicable physical operators.
                                val physicalPlans = enumerator.enumerate(plan1, plan2, operator)

                                // Sort these physical plans by cost, en pick the cheapest one.
                                val currentPlan = physicalPlans.sortedBy { it.cost() }.first()

                                // We have to associate our physical plan to the logical plan where subtree1 and
                                // subtree2 are children of the operator for which they are contained in logicalPlan.
                                val subtree = subtree1.mergeAndFlatten(subtree2, operator)

                                // The cheapestPlan we already have for our subtree, or null.
                                val cheapestPlan = cheapestPhysicalPlans[subtree.hashCode()]

                                // Save our currentPlan as the cheapestPlan if its cost is cheaper than the known plan.
                                if (cheapestPlan == null || currentPlan.cost() < cheapestPlan.cost()) {
                                    cheapestPhysicalPlans.put(subtree.hashCode(), currentPlan)
                                }
                            }
                        }
                    }
                }
            }
        }

        // If everything went fine, we should have calculated the cheapest physical plan for our logicalPlan.
        val physicalPlan = cheapestPhysicalPlans.getValue(logicalPlan.hashCode())

        return physicalPlan
    }
}
