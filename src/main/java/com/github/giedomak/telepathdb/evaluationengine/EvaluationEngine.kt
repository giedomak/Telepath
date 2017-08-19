/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.evaluationengine

import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.physicallibrary.PhysicalLibrary
import com.github.giedomak.telepathdb.utilities.Logger
import java.util.stream.Stream

/**
 * Evaluate a physical-plan in order to get results from the path-index.
 */
class EvaluationEngine(private val kPathIndex: KPathIndex) {

    fun evaluate(physicalPlan: PhysicalPlan): Stream<Path> {

        if (physicalPlan.isLeaf) return Stream.empty()

        // Make sure we do an Postorder treewalk, this way we gather all the information from the leafs first
        for (child in physicalPlan.children) {
            evaluate(child as PhysicalPlan)
        }

        // Perform the Operations
        val results = when (physicalPlan.operator) {

            PhysicalPlan.LOOKUP -> {
                // Collect results from the leafs and add them in the intermediateResults HashMap
                val search = PathPrefix(physicalPlan.pathIdOfLookup())
                kPathIndex.search(search)
            }

            ParseTree.UNION -> PhysicalLibrary.union(getChild(physicalPlan, 0), getChild(physicalPlan, 1))

//            ParseTree.CONCATENATION -> results = PhysicalLibrary.concatenation(getChild(physicalPlan, 0), getChild(physicalPlan, 1))

            else -> throw IllegalArgumentException("EvaluationEngine: operator not yet implemented for " + physicalPlan.nodeRepresentation + "!")
        }

        if (physicalPlan.isRoot) {
            // Make sure we return the stream when this node was the root
            return results
        } else {
            Logger.debug("Itermediateresult: " + physicalPlan.nodeRepresentation)
            physicalPlan.memoryManagerId = MemoryManager.add(results)
        }

        return Stream.empty()
    }

    private fun getChild(physicalPlan: PhysicalPlan, index: Int): Stream<Path> {
        return MemoryManager[(physicalPlan.getChild(index)!! as PhysicalPlan).memoryManagerId]
    }
}
