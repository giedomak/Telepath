/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.evaluationengine

import com.github.giedomak.telepathdb.datamodels.ParseTree
import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.datamodels.utilities.Logger
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.physicallibrary.PhysicalLibrary
import java.util.stream.Stream

/**
 * Evaluate a physical-plan in order to get results from the path-index.
 */
class EvaluationEngine(private val kPathIndex: KPathIndex) {

    fun evaluate(parseTree: ParseTree): Stream<Path> {

        if (parseTree.isLeaf) return Stream.empty()

        // Make sure we do an Postorder treewalk, this way we gather all the information from the leafs first
        for (child in parseTree.children) {
            evaluate(child)
        }

        val results: Stream<Path>

        // Perform the Operations
        when (parseTree.operatorId) {

            ParseTree.LOOKUP -> {
                // Collect results from the leafs and put them in the intermediateResults HashMap
                val edges = parseTree.children.map { it.leaf!! }
                val pathIdentifier = PathIdentifierStore.getPathIdByEdges(edges)
                val search = PathPrefix(pathIdentifier)
                results = kPathIndex.search(search)
            }

            ParseTree.UNION -> results = PhysicalLibrary.union(getChild(parseTree, 0), getChild(parseTree, 1))

            ParseTree.CONCATENATION -> results = PhysicalLibrary.concatenation(getChild(parseTree, 0), getChild(parseTree, 1))

            else -> throw IllegalArgumentException("EvaluationEngine: operatorId not yet implemented for " + parseTree.leafOrOperator + "!")
        }

        if (parseTree.isRoot) {
            // Make sure we return the stream when this node was the root
            return results
        } else {
            Logger.debug("Itermediateresult: " + parseTree.leafOrOperator)
            MemoryManager.put(parseTree.id, results)
        }

        return Stream.empty()
    }

    private fun getChild(parseTree: ParseTree, index: Int): Stream<Path> {
        return MemoryManager.get(parseTree.getChild(index)!!.id)
    }
}
