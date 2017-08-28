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
 * Hash-join.
 */
class HashJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    /**
     * Join two streams of Paths following the HashJoin algorithm and by using our MemoryManager.
     *
     * @param stream1 First stream of paths we'll join on its lastNode().
     * @param stream2 Second stream of paths we'll join on its firstNode().
     * @return A stream with the concatenated paths of stream1 and stream2.
     */
    override fun evaluate(): PathStream {
        return OpenHashJoin(firstChild.evaluate(), lastChild.evaluate(), physicalPlan.query.telepathDB.pathIdentifierStore).evaluate()
    }

    /**
     * Cost of Hash-join.
     */
    override fun cost(): Long {
        return 2 * (firstChild.cardinality() + lastChild.cardinality())
    }
}
