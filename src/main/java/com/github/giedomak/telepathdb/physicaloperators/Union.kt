package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import java.util.stream.Stream

/**
 * Union physical operator.
 *
 * @property physicalPlan The physical plan where we have to perform the union on its children.
 */
class Union(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    /**
     * Evaluate the union.
     *
     * Default behaviour is to filter out duplicates, just like in other DBMS.
     *
     * @return A path stream with the resulting elements.
     */
    override fun evaluate(): PathStream {
        return PathStream(
                physicalPlan.query.telepathDB,
                Stream.concat(
                        firstChild.evaluate().paths,
                        lastChild.evaluate().paths
                ).distinct()
        )
    }

    /**
     * Cost of the union operation.
     */
    override fun cost(): Long {

        // Cost to produce results.
        val myCost = firstChild.cardinality + lastChild.cardinality

        // Our input sets might be intermediate results, so take their cost into account.
        val intermediateResultsCost = firstChild.cost() + lastChild.cost()

        return myCost + intermediateResultsCost
    }

}