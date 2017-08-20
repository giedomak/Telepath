package com.github.giedomak.telepathdb.physicallibrary.operators

import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

class IndexLookup(
        private val physicalPlan: PhysicalPlan
) : PhysicalOperator {

    override fun evaluate(): PathStream {
        return PathStream(
                physicalPlan.query.telepathDB.kPathIndex.search(
                        PathPrefix(
                                physicalPlan.pathIdOfChildren()
                        )
                )
        )
    }

    override fun cost(): Long {
        return physicalPlan.cardinality()
    }

}