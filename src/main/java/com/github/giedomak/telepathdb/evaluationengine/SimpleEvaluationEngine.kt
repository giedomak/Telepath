/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.evaluationengine

import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.physicallibrary.joins.HashJoin
import com.github.giedomak.telepathdb.utilities.Logger

/**
 * Evaluate a physical-plan in order to get results from the path-index.
 */
class SimpleEvaluationEngine(private val kPathIndex: KPathIndex) : EvaluationEngine {

    override fun evaluate(physicalPlan: PhysicalPlan): PathStream {

        if (physicalPlan.isLeaf) return PathStream()

        // Make sure we do an Postorder treewalk, this way we gather all the information from the leafs first
        for (child in physicalPlan.children) {
            evaluate(child)
        }

        // Perform the Operations
        val results = when (physicalPlan.operator) {

            PhysicalPlan.INDEXLOOKUP -> {
                // Collect results from the leafs and add them in the intermediateResults HashMap
                val search = PathPrefix(physicalPlan.pathIdOfChildren())
                kPathIndex.search(search)
            }

//            PhysicalPlan.UNION -> PhysicalLibrary.union(getChild(physicalPlan, 0), getChild(physicalPlan, 1))

            PhysicalPlan.HASHJOIN -> HashJoin(getChild(physicalPlan, 0), getChild(physicalPlan, 1)).evaluate()

            else -> TODO("SimpleEvaluationEngine: operator not yet implemented for " + physicalPlan.nodeRepresentation + "!")
        }

        Logger.debug("Itermediateresult: " + physicalPlan.nodeRepresentation)
        physicalPlan.memoryManagerId = MemoryManager.add(results)
        return PathStream(MemoryManager[physicalPlan.memoryManagerId])
    }

    private fun getChild(physicalPlan: PhysicalPlan, index: Int): PathStream {
        return PathStream(MemoryManager[(physicalPlan.getChild(index)!!).memoryManagerId])
    }
}
