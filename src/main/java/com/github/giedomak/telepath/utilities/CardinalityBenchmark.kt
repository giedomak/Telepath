package com.github.giedomak.telepath.utilities

import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan


/**
 * The DynamicProgrammingPlanner uses the DPsize algorithm for inspiration.
 *
 * We generate the best physical plan for smaller subtrees of a given logical plan. Which will guarantee we
 * will have the cheapest physical plan for the full logical plan.
 */
object CardinalityBenchmark {

    /**
     * Generate the cheapest physical plan for a given flattened logical plan.
     *
     * @param logicalPlan A flattened logical plan for which we have to generate the cheapest physical plan.
     * @return The cheapest physical plan for the given flattened logical plan.
     */
    fun generate(logicalPlan: LogicalPlan): Sequence<PhysicalPlan> {

        val enumerator = logicalPlan.query.telepath.enumerator

        // We have to iterate until we reach the size of the logicalPlan we are searching a physical plan for.
        val n = logicalPlan.getSize()

        val cheapestPhysicalPlans = hashMapOf<LogicalPlan, MutableSet<PhysicalPlan>>()

        // Init the bestPlan for all subtrees of size 1 to:
        //    INDEX_LOOKUP
        //         |
        //        LEAF
        logicalPlan.subtreesOfSize(1).forEach {
            cheapestPhysicalPlans.put(
                    it,
                    mutableSetOf(PhysicalPlan(logicalPlan.query, it.leaf!!))
            )
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
                                val plan1 = cheapestPhysicalPlans.getValue(subtree1)
                                val plan2 = cheapestPhysicalPlans.getValue(subtree2)

                                // Enumerate or logical operator into applicable physical operators.
                                val physicalPlans = mutableListOf<PhysicalPlan>()

                                plan1.forEach { p1 -> plan2.forEach { p2 -> physicalPlans.addAll(enumerator.enumerate(p1, p2, operator)) }}

                                // We have to associate our physical plan to the logical plan where subtree1 and
                                // subtree2 are children of the operator for which they are contained in logicalPlan.
                                val subtree = subtree1.mergeAndFlatten(subtree2, operator)

                                cheapestPhysicalPlans.compute(subtree, { _, value -> value?.addAll(physicalPlans); value ?: physicalPlans.toMutableSet()})

                            }
                        }
                    }
                }
            }
        }

        // If everything went fine, we should have calculated the cheapest physical plan for our logicalPlan.
        return cheapestPhysicalPlans.getValue(logicalPlan).asSequence()
    }
}
