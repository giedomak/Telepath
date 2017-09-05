package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.cardinalityestimation.synopsis.Synopsis
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator

class SynopsisCardinalityEstimation(private val kPathIndex: KPathIndex) : CardinalityEstimation {

    override fun getCardinality(physicalPlan: PhysicalPlan): Long {

        // TODO: could be different join operators mixed
        val clone = physicalPlan.clone().flatten()

        // See if we got one of these after flattening:
        //
        //             HASH_JOIN
        //             /       \
        //     INDEX_LOOKUP  INDEX_LOOKUP
        //       /  |  \        /   \
        //      a   b   c      d     e      <--- EDGES
        if (clone.operator in PhysicalOperator.JOIN_OPERATORS && clone.height() == 2) {

            val edges: List<Edge> = clone.children.flatMap { it.children.map { it.leaf!! } }

            // We can get | T r/l1 | from our Synopsis.
            var cardinality = Synopsis.pairs(Pair(edges[0], edges[1])).toFloat()

            // | T r/l1/l2 | = | T r/l1 | * ( l1/l2.two / l1.in )
            for (index in 2 until edges.size) {

                val l1 = edges[index - 1]
                val l2 = edges[index]

                cardinality *= Synopsis.two(Pair(l1, l2)) / Synopsis.`in`(l1).toFloat()

            }

            return cardinality.toLong()
        }

        // We got no use here, switch to the KPathIndexCardinalityEstimation.
        return KPathIndexCardinalityEstimation(kPathIndex).getCardinality(physicalPlan)
    }
}