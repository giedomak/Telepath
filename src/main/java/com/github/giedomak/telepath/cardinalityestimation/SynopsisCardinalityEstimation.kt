package com.github.giedomak.telepath.cardinalityestimation

import com.github.giedomak.telepath.cardinalityestimation.synopsis.Synopsis
import com.github.giedomak.telepath.datamodels.graph.Edge
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.physicaloperators.PhysicalOperator

/**
 * Cardinality estimation using a synopsis.
 *
 * The [Synopsis] is a statistics store which holds information regarding concatenations.
 */
class SynopsisCardinalityEstimation(kPathIndex: KPathIndex) : CardinalityEstimation {

    var synopsis = Synopsis()

    init {
        // Set the callback on after insert
        kPathIndex.insertCallback = synopsis::handleInsertion
    }

    /**
     * Return the cardinality of a given physical plan.
     */
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
        if (physicalPlan.height() > 1 && physicalPlan.postOrderTraversal().none { it.operator == PhysicalOperator.UNION }) {

            val modified = physicalPlan.clone()

            modified.postOrderTraversal()
                    .filter { it.operator == PhysicalOperator.SORT_MERGE_JOIN }
                    .forEach { it.children.first().children.forEach { it.leaf = it.leaf!!.inverse() } }

            val edges: List<Edge> = modified.postOrderTraversal().filter { it.isLeaf }.map { it.leaf!! }.toList()

            // We can get | T r/l1 | from our Synopsis.
            var cardinality = synopsis.pairs(Pair(edges[0], edges[1])).toFloat()

            // | T r/l1/l2 | = | T r/l1 | * ( l1/l2.two / l1.in )
            for (index in 2 until edges.size) {

                val l1 = edges[index - 1]
                val l2 = edges[index]

                cardinality *= synopsis.two(Pair(l1, l2)) / synopsis.`in`(l1).toFloat()

            }

            // Return the result
            return cardinality.toLong()
        } else if (clone.operator == PhysicalOperator.INDEX_LOOKUP && clone.height() == 1) {

            val edges: List<Edge> = clone.children.map { it.leaf!! }

            if (edges.size == 1) {
                return synopsis.pairs(edges.first()).toLong()
            } else if (edges.size == 2) {
                return synopsis.pairs(Pair(edges.first(), edges.last())).toLong()
            }
        }

        // We got no use here, switch to the KPathIndexCardinalityEstimation.
        return KPathIndexCardinalityEstimation(physicalPlan.query.telepath.kPathIndex).getCardinality(physicalPlan)
    }
}