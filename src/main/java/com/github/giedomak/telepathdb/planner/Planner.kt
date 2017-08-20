/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan
import com.github.giedomak.telepathdb.utilities.Logger

/**
 * Generate the best physical plan for a given [ParseTree].
 */
object Planner {

    private fun enumerateConcatenation(tree1: PhysicalPlan, tree2: PhysicalPlan): List<PhysicalPlan> {

        val physicalPlans = mutableListOf<PhysicalPlan>()

        // Check if an IndexLookup is applicable
        val plan = tree1.merge(tree2, PhysicalPlan.LOOKUP).flatten() as PhysicalPlan

        // TODO: k should be k-index dependent
        if (plan.level() == 2 && plan.children.size <= 3) {
            physicalPlans.add(plan)
        }

        physicalPlans.add(tree1.merge(tree2, PhysicalPlan.HASHJOIN))
        physicalPlans.add(tree1.merge(tree2, PhysicalPlan.NESTEDLOOPJOIN))

        return physicalPlans
    }


    fun generate(tree: ParseTree): PhysicalPlan {

        tree.flatten()

        val n = tree.getSize()

        val bestPlans = hashMapOf<Int, PhysicalPlan>()

        // Init the BestPlan for all sub-paths of size 1
        tree.subtreesOfSize(1).forEach { bestPlans.put(it.hashCode(), PhysicalPlan(it.leaf!!)) }

        for (size in 2..n) {

            for (i in 1..(size - 1)) {

                val j = size - i

                for (s1 in tree.subtreesOfSize(i)) {

                    for (s2 in tree.subtreesOfSize(j)) {

                        Logger.debug("s1: $s1")
                        Logger.debug("s2: $s2")

                        val p1 = bestPlans.getValue(s1.hashCode())
                        val p2 = bestPlans.getValue(s2.hashCode())

                        if (tree.containsSubtreesThroughOperator(s1, s2, ParseTree.CONCATENATION)) {

                            val subtree = s1.mergeAndFlatten(s2, ParseTree.CONCATENATION)

                            val plans = enumerateConcatenation(p1, p2).sortedBy { it.cost() }.toList()
                            val newPlan = plans.first()

                            Logger.debug("LETS GO")
                            plans.forEach { it.print(); Logger.debug(it.cost()) }

                            val bestPlan = bestPlans.getOrPut(subtree.hashCode(), { newPlan })

                            Logger.debug("subtree: $subtree")
                            Logger.debug("bestPlan: $bestPlan")
                            bestPlan.print()
                            Logger.debug("cost: " + bestPlan.cost())

                            if (newPlan.cost() < bestPlan.cost()) {
                                bestPlans.put(subtree.hashCode(), newPlan)
                                Logger.debug("CHEAPEST!")
                            }
                        }
                    }
                }
            }
        }

        Logger.debug("Final: $tree")

        return bestPlans.getValue(tree.hashCode())
    }
}
