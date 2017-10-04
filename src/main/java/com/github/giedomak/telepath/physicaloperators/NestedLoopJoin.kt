/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.physicaloperators

import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore

/**
 * Nested-loop-join.
 */
class NestedLoopJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    override fun evaluate(): PathStream {

        // Because we are doing a nested loop, we have to ensure it is materialized since that wil give us a Supplier.
        val pathSupplier = lastChild.evaluate().ensureMaterialization().pathSupplier

        // Basically we are doing a nested loop to do an inner-join and concatenate the paths.
        return PathStream(
                physicalPlan.query.telepath,
                firstChild.evaluate().paths.flatMap { v1 ->
                    pathSupplier.get()
                            .filter { v2 -> v1.nodes.last() == v2.nodes.first() }
                            .map { v2 -> PathIdentifierStore.concatenatePaths(v1, v2) }
                            .filter { it != null }
                }
        )
    }

    /**
     * The cost of our nested loop join implementation.
     */
    override fun cost(): Long {
        // The cost to produce results
        val myCost = Math.pow(lastChild.cardinality.toDouble(), firstChild.cardinality.toDouble()).toLong()

        // Our input sets might be intermediate results, so take their cost into account.
        val cost1 = firstChild.cost()
        val cost2 = lastChild.cost()

        // Overflow check
        if (myCost == Long.MAX_VALUE || cost1 == Long.MAX_VALUE || cost2 == Long.MAX_VALUE) return Long.MAX_VALUE

        return Long.MAX_VALUE
        return myCost + cost1 + cost2
    }
}
