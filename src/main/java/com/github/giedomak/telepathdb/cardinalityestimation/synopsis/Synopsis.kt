package com.github.giedomak.telepathdb.cardinalityestimation.synopsis

import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Path

object Synopsis {

    private val syn1 = Syn1()
    private val syn2 = Syn2()

    /**
     * Number of nodes in G which have outgoing [edge].
     */
    fun out(edge: Edge): Int {
        return syn1.outMap[edge]?.size ?: 0
    }

    /**
     * Number of nodes in G which have outgoing [edges].
     */
    fun out(edges: Pair<Edge, Edge>): Int {
        return syn2.outMap[edges]?.size ?: 0
    }

    /**
     * Number of nodes in G which have incoming [edge].
     */
    fun `in`(edge: Edge): Int {
        return syn1.inMap[edge]?.size ?: 0
    }

    /**
     * Number of nodes in G which have incoming [edges].
     */
    fun `in`(edges: Pair<Edge, Edge>): Int {
        return syn2.inMap[edges]?.size ?: 0
    }

    /**
     * Number of nodes in G which have incoming edge labeled l1 and outgoing edge labeled l2.
     *
     * Where l1 is the first element of the [edges] pair, and l2 is the second element of the pair.
     */
    fun middle(edges: Pair<Edge, Edge>): Int {
        return syn2.middleMap[edges]?.size ?: 0
    }

    /**
     * Number of paths in G labeled by [edge].
     */
    fun paths(edge: Edge): Int {
        return syn1.pathsMap[edge] ?: 0
    }

    /**
     * Number of paths in G labeled with l1/l2.
     */
    fun paths(edges: Pair<Edge, Edge>): Int {
        return syn2.pathsMap[edges] ?: 0
    }

    /**
     * Number of pairs in G labeled by [edge].
     */
    fun pairs(edge: Edge): Int {
        return syn1.pairsMap[edge]?.size ?: 0
    }

    /**
     * Number of pairs in G labeled by l1/l2.
     */
    fun pairs(edges: Pair<Edge, Edge>): Int {
        return syn2.pairsMap[edges]?.size ?: 0
    }

    /**
     * Number of paths labeled l1 from nodes in out to nodes in middle.
     */
    fun one(edges: Pair<Edge, Edge>): Int {
        return syn2.oneMap[edges] ?: 0
    }

    /**
     * Number of paths labeled l2 from nodes in middle to nodes in in.
     */
    fun two(edges: Pair<Edge, Edge>): Int {
        return syn2.twoMap[edges] ?: 0
    }

    /**
     * Update our Synopsis on new insertions into the index.
     *
     * This callback will be invoked when a new path is added to the index.
     */
    fun handleInsertion(path: Path) {

        // Update our SYNS
        when (path.nodes.size) {

            2 -> syn1.handleInsertion(path)
            3 -> syn2.handleInsertion(path)

        }

    }

    /**
     * Expensive function when updating SYN2; make sure to invoke this function manually when doing batch actions.
     */
    fun afterMath() {

        syn2.pairsMap.keys.forEach { edgePair ->

            // ONE
            syn2.oneMap.compute(edgePair, { _, _ ->
                syn1.pairsMap[edgePair.first]!!
                        .filter { syn2.outMap[edgePair]!!.contains(it.first) }
                        .filter { syn2.middleMap[edgePair]!!.contains(it.second) }
                        .size
            })

            // TWO
            syn2.twoMap.compute(edgePair, { _, _ ->
                syn1.pairsMap[edgePair.second]!!
                        .filter { syn2.middleMap[edgePair]!!.contains(it.first) }
                        .filter { syn2.inMap[edgePair]!!.contains(it.second) }
                        .size
            })
        }
    }
}