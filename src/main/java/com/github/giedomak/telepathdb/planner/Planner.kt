/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.costmodel.SimpleCostModel
import com.github.giedomak.telepathdb.datamodels.ParseTree
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import java.util.stream.Collectors

/**
 * Generate the best physical plan for a given [ParseTree].
 */
object Planner {

    fun generate(tree: ParseTree): ParseTree {

        // Get an array of labels forming the label path
        // e.g. ['knows', 'worksFor', 'loves']
        val labelPath = tree.postOrderTreeWalk()
                .filter { t -> t.isLeaf }
                .map { it.leafOrOperator }
                .collect(Collectors.toList())

        val n = labelPath.size
        val k = 3

        val bestPlans = hashMapOf<Long, ParseTree>()

        // Init the BestPlan for all sub-paths of size 1
        labelPath.stream().forEach { label ->
            val pathId = PathIdentifierStore.getPathIdByEdgeLabel(label)
            bestPlans.put(
                    pathId,
                    ParseTree.createLookupTree(pathId)
            )
        }

        for (size in 2..n) {

            for (offset in 0..n - size) {

                val Lsub = labelPath.subList(offset, offset + size)
                val LsubId = PathIdentifierStore.getPathIdByEdgeLabel(Lsub)

                if (size <= k && !bestPlans.containsKey(LsubId)) {
                    bestPlans.put(LsubId, ParseTree.createLookupTree(LsubId))
                }

                if (bestPlans.containsKey(LsubId)) {
                    continue
                }

                for (split in 1..size - 1) {

                    val L1 = labelPath.subList(offset, offset + split)
                    val L1Id = PathIdentifierStore.getPathIdByEdgeLabel(L1)

                    val L2 = labelPath.subList(offset + split, offset + size)
                    val L2Id = PathIdentifierStore.getPathIdByEdgeLabel(L2)

                    val p1 = bestPlans[L1Id]!!
                    val p2 = bestPlans[L2Id]!!
                    val currPlan = ParseTree.createConcatenationTree(p1, p2)

                    if (!bestPlans.containsKey(LsubId) || SimpleCostModel.cost(currPlan) < SimpleCostModel.cost(bestPlans[LsubId]!!)) {
                        bestPlans.put(LsubId, currPlan)
                    }
                }
            }
        }

        val pathId = PathIdentifierStore.getPathIdByEdgeLabel(labelPath)
        return bestPlans[pathId]!!
    }
}
