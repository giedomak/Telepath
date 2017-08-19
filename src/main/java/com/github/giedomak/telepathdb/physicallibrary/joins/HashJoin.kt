/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicallibrary.joins

import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathStream
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.physicallibrary.BinaryPhysicalOperator
import com.github.giedomak.telepathdb.physicallibrary.PhysicalOperator
import com.github.giedomak.telepathdb.utilities.Logger
import java.util.stream.Stream

/**
 * Hash-join.
 */
class HashJoin(
        private val stream1: PathStream? = null,
        private val stream2: PathStream? = null
) : PhysicalOperator {

    /**
     * Join two streams of Paths following the HashJoin algorithm and by using our MemoryManager.
     *
     * @param stream1 First stream of paths we'll join on its lastNode().
     * @param stream2 Second stream of paths we'll join on its firstNode().
     * @return A stream with the concatenated paths of stream1 and stream2.
     */
     override fun evaluate(): Stream<Path> {

        // Make sure we get a free slot in the MemoryManager
        val offset = MemoryManager.maxId + 1

        // Put all Paths from stream1 into a HashMap with the lastNode() as key
        stream1!!.paths.forEach { MemoryManager[offset + it.nodes.last().id] = it }

        Logger.debug("Done creating the hashTable, now concatenating")

        // Get all Paths from the HashMap which have the firstNode() as key, and concatenate
        return stream2!!.paths.flatMap { v2 ->
            MemoryManager[offset + v2.nodes.first().id]
                    .map { v1 -> PathIdentifierStore.concatenatePaths(v1, v2) }
        }
    }

    companion object : BinaryPhysicalOperator {

        /**
         * Cost of Hash-join.
         */
        override fun cost(cardinality1: Long, cardinality2: Long): Long {
            return 2 * (cardinality1 + cardinality2)
        }

    }
}
