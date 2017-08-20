/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicallibrary.operators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.utilities.Logger

/**
 * Hash-join.
 */
class HashJoin() : PhysicalOperator {

    private var physicalPlan: PhysicalPlan? = null
    private var stream1: PathStream? = null
    private var stream2: PathStream? = null

    constructor(physicalPlan: PhysicalPlan) : this() {
        this.physicalPlan = physicalPlan
    }

    constructor(stream1: PathStream, stream2: PathStream) : this() {
        this.stream1 = stream1
        this.stream2 = stream2
    }

    private val leftChild get() = physicalPlan!!.children.first()
    private val rightChild get() = physicalPlan!!.children.last()

    private val finalStream1 get() = stream1 ?: leftChild.physicalOperator.evaluate()
    private val finalStream2 get() = stream2 ?: rightChild.physicalOperator.evaluate()

    /**
     * Join two streams of Paths following the HashJoin algorithm and by using our MemoryManager.
     *
     * @param stream1 First stream of paths we'll join on its lastNode().
     * @param stream2 Second stream of paths we'll join on its firstNode().
     * @return A stream with the concatenated paths of stream1 and stream2.
     */
    override fun evaluate(): PathStream {

        // Make sure we get a free slot in the MemoryManager
        val offset = MemoryManager.maxId + 1

        // Put all Paths from stream1 into a HashMap with the lastNode() as key
        finalStream1.paths.forEach { MemoryManager[offset + it.nodes.last().id] = it }

        Logger.debug("Done creating the hashTable, now concatenating")

        // Get all Paths from the HashMap which have the firstNode() as key, and concatenate
        return PathStream(
                finalStream2.paths.flatMap { v2 ->
                    MemoryManager[offset + v2.nodes.first().id]
                            .map { v1 -> PathIdentifierStore.concatenatePaths(v1, v2) }
                }
        )
    }

    /**
     * Cost of Hash-join.
     */
    override fun cost(): Long {
        return 2 * (leftChild.cardinality() + rightChild.cardinality())
    }
}
