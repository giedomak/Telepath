/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.utilities.Logger

/**
 * Open hash-join to maintain compatibility with concatenating streams instead of evaluating physical plans.
 */
class OpenHashJoin(private val stream1: PathStream, private val stream2: PathStream, private val pathIdentifierStore: PathIdentifierStore = PathIdentifierStore) {

    /**
     * Join two streams of Paths following the HashJoin algorithm and by using our MemoryManager.
     *
     * @param stream1 First stream of paths we'll join on its lastNode().
     * @param stream2 Second stream of paths we'll join on its firstNode().
     * @return A stream with the concatenated paths of stream1 and stream2.
     */
    fun evaluate(): PathStream {

        // Make sure we get a free slot in the MemoryManager
        val offset = MemoryManager.maxId + 1

        // Put all Paths from stream1 into a HashMap with the lastNode() as key
        stream1.paths.forEach { MemoryManager[offset + it.nodes.last().id] = it }

        Logger.debug("Done creating the hashTable, now concatenating")

        // Get all Paths from the HashMap which have the firstNode() as key, and concatenate
        return PathStream(
                stream2.paths.flatMap { v2 ->
                    MemoryManager[offset + v2.nodes.first().id]
                            .map { v1 -> pathIdentifierStore.concatenatePaths(v1, v2) }
                }
        )
    }
}
