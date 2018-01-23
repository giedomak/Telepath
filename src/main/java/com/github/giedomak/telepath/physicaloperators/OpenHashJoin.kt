/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.physicaloperators

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.utilities.Logger
import java.util.*
import java.util.stream.StreamSupport

/**
 * Open hash-join to maintain compatibility with concatenating streams instead of evaluating physical plans.
 */
class OpenHashJoin(
        private val stream1: PathStream,
        private val stream2: PathStream,
        private val telepath: Telepath = Telepath,
        private val materialize: Boolean = true,
        private val cardinality: Int = 100,
        private val parallel: Boolean = true
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
//        val salt = Random().nextLong()

        val hashMap = HashMap<Int, MutableList<Path>>(cardinality)

        // Put all Paths from stream1 into a HashMap with the lastNode() as key
        // stream1.paths.forEach { telepathDB.memoryManager[salt + it.nodes.last().id] = it }

        Logger.debug("Creating the hashMap right now...")

        stream1.paths.forEach { hashMap.compute(it.nodes.last().id.toInt(), { _, value -> value?.add(it); value ?: mutableListOf(it) }) }

        Logger.debug("Done creating the hashMap, now concatenating")

        // Get all Paths from the HashMap which have the firstNode() as key, and concatenate.
        // By passing telepath as the second argument of the PathStream constructor, it gets materialized again.
        return PathStream(
                telepath,
                stream2.paths.flatMap { v2 ->
                    StreamSupport.stream(hashMap.getOrDefault(v2.nodes.first().id.toInt(), emptyList<Path>()).spliterator(), parallel)
                            .map { v1 -> telepath.pathIdentifierStore.concatenatePaths(v1 as Path, v2) }
                            .filter { it != null }
                }
                , materialize)
    }
}
