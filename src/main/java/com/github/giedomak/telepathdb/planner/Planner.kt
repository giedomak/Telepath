/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan

/**
 * Generate the best physical plan for a given [ParseTree].
 */
object Planner {

    fun enumeratePhysicalPlans(operator: Int, tree1: PhysicalPlan, tree2: PhysicalPlan): List<PhysicalPlan> {

        // Switch case on operator
        return when (operator) {

            ParseTree.CONCATENATION -> enumerateConcatenation(tree1, tree2)

            else -> TODO("Work in progress")
        }
    }

    private fun enumerateConcatenation(tree1: PhysicalPlan, tree2: PhysicalPlan): List<PhysicalPlan> {

        val physicalPlans = mutableListOf<PhysicalPlan>()

        // Check if an IndexLookup is applicable
        val plan = tree1.merge(tree2, PhysicalPlan.LOOKUP).flatten() as PhysicalPlan

        if (plan.level() == 2 && plan.children.size <= TelepathDB.kPathIndex.k) {
            physicalPlans.add(plan)
        }

        physicalPlans.add(tree1.merge(tree2, PhysicalPlan.HASHJOIN))
        physicalPlans.add(tree1.merge(tree2, PhysicalPlan.NESTEDLOOPJOIN))

        return physicalPlans
    }


    fun generate(tree: ParseTree): PhysicalPlan {

        val n = tree.getSize()

        val bestPlans = hashMapOf<ParseTree, PhysicalPlan>()

        // Init the BestPlan for all sub-paths of size 1
        tree.subtreesOfSize(1).forEach { bestPlans.put(it, PhysicalPlan(it.leaf!!)) }

        for (size in 2..n) {

            val i = 1
            val j = size - i

            for (s1 in tree.subtreesOfSize(i)) {

                for (s2 in tree.subtreesOfSize(j)) {

                    val p1 = bestPlans.getValue(s1)
                    val p2 = bestPlans.getValue(s2)

                    if (tree.containsSubtreesThroughOperator(s1, s2, ParseTree.CONCATENATION)) {

                        val subtree = s1.mergeAndFlatten(s2, ParseTree.CONCATENATION)

                        val newPlan = enumerateConcatenation(p1, p2).sortedBy { it.cost() }.first()

                        val bestPlan = bestPlans.getOrPut(subtree, { newPlan })

                        if (newPlan.cost() < bestPlan.cost()) {
                            bestPlans.put(subtree, newPlan)
                        }
                    }
                }
            }
        }
        return bestPlans.getValue(tree)
    }
}
