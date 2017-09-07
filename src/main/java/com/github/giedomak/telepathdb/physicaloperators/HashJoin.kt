/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

/**
 * Hash-join physical operator.
 *
 * @property physicalPlan The physical plan holds information regarding the sets on which to operate on.
 * @property firstChild The first set of data to operate on, which is a [PhysicalOperator] itself.
 * @property lastChild The last set of data to operate on, which is a [PhysicalOperator] itself.
 */
class HashJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    /**
     * Evaluate the hash-join.
     *
     * @return A stream with the concatenated paths.
     */
    override fun evaluate(): PathStream {
        return OpenHashJoin(
                firstChild.evaluate(),
                lastChild.evaluate(),
                physicalPlan.query.telepathDB
        ).evaluate()
    }

    /**
     * Cost of Hash-join.
     */
    override fun cost(): Long {

        // The cost to produce results, i.e. 2 * (M + N)
        val myCost = 2 * (firstChild.cardinality + lastChild.cardinality)

        // Our input sets might be intermediate results, so take their cost into account.
        val cost1 = firstChild.cost()
        val cost2 = lastChild.cost()

        // Overflow check
        if (myCost == Long.MAX_VALUE || cost1 == Long.MAX_VALUE || cost2 == Long.MAX_VALUE) return Long.MAX_VALUE

        return myCost + cost1 + cost2
    }
}
