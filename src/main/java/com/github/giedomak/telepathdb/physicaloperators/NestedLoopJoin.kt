/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.memorymanager.MemoryManager

/**
 * Nested-loop-join.
 */
class NestedLoopJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    override fun evaluate(): PathStream {

        // Because we are doing a nested loop, we have to create the stream again for each new iteration.
        val streamSupplier = MemoryManager.streamSupplier(lastChild.evaluate().paths)

        // Basically we are doing a nested loop to do an inner-join and concatenate the paths.
        return PathStream(
                firstChild.evaluate().paths.flatMap { v1 ->
                    streamSupplier.get()
                            .filter { v2 -> v1.nodes.last() == v2.nodes.first() }
                            .map { v2 -> PathIdentifierStore.concatenatePaths(v1, v2) }
                }
        )
    }

    override fun cost(): Long {
        // return Math.pow(cardinality2.toDouble(), cardinality1.toDouble()).toLong()
        return Long.MAX_VALUE
    }
}
