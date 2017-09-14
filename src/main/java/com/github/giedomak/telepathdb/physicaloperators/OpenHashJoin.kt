/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.utilities.Logger
import java.util.*

/**
 * Open hash-join to maintain compatibility with concatenating streams instead of evaluating physical plans.
 */
class OpenHashJoin(
        private val stream1: PathStream,
        private val stream2: PathStream,
        private val telepathDB: TelepathDB = TelepathDB
) {

    /**
     * Join two streams of Paths following the HashJoin algorithm and by using our SimpleMemoryManager.
     *
     * @param stream1 First stream of paths we'll join on its lastNode().
     * @param stream2 Second stream of paths we'll join on its firstNode().
     * @return A stream with the concatenated paths of stream1 and stream2.
     */
    fun evaluate(): PathStream {

        // TODO: Make sure the memoryManager can guarantee a free spot
        val salt = Random().nextLong()

        // Put all Paths from stream1 into a HashMap with the lastNode() as key
        stream1.paths.forEach { telepathDB.memoryManager[salt + it.nodes.last().hashCode().toLong()] = it }

        Logger.debug("Done creating the hashTable, now concatenating")

        // Get all Paths from the HashMap which have the firstNode() as key, and concatenate.
        // By passing telepathDB as the second argument of the PathStream constructor, it gets materialized again.
        return PathStream(
                telepathDB,
                stream2.paths.flatMap { v2 ->
                    telepathDB.memoryManager[salt + v2.nodes.first().hashCode().toLong()]
                            .map { v1 -> telepathDB.pathIdentifierStore.concatenatePaths(v1, v2) }
                            .filter { it != null }
                }
        )
    }
}
