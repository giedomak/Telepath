package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

/**
 * Physical operator to lookup paths in the kPathIndex.
 *
 * Let's say we've got this physical plan:
 *
 *      INDEX_LOOKUP
 *        /  |  \
 *       a   b   c
 *
 * Then `a - b - c` forms the labeled path for which we are searching for paths in de index.
 *
 * @property physicalPlan The physical plan which holds the leafs which make up the pathId.
 */
class IndexLookup(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    /**
     * Evaluate the index lookup and stream the results.
     *
     * @return PathStream with the results of the index lookup.
     */
    override fun evaluate(): PathStream {
        return PathStream(
                physicalPlan.query.telepathDB.kPathIndex.search(
                        PathPrefix(
                                physicalPlan.pathIdOfChildren()
                        )
                )
        )
    }

    /**
     * Cost of an index lookup is very cheap.
     */
    override fun cost(): Long {
        return physicalPlan.cardinality()
    }
}