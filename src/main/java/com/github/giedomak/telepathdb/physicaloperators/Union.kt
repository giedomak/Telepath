package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import java.util.stream.Stream

class Union(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    override fun evaluate(): PathStream {
        return PathStream(
                Stream.concat(
                        firstChild.evaluate().paths,
                        lastChild.evaluate().paths
                )
        )
    }

    override fun cost(): Long {
        TODO("not implemented")
    }

}