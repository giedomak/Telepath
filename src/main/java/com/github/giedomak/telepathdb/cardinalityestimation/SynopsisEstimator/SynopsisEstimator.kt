package com.github.giedomak.telepathdb.cardinalityestimation.SynopsisEstimator

import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore

object SynopsisEstimator {

    // SYN1
    private val outMap = hashMapOf<Edge, HashSet<Node>>()
    private val inMap = hashMapOf<Edge, HashSet<Node>>()
    private val pathsMap = hashMapOf<Edge, Int>()
    private val pairsMap = hashMapOf<Edge, HashSet<Pair<Node, Node>>>()

    // SYN2
    private val outMap2 = hashMapOf<Pair<Edge, Edge>, HashSet<Node>>()
    private val inMap2 = hashMapOf<Pair<Edge, Edge>, HashSet<Node>>()
    private val middleMap2 = hashMapOf<Pair<Edge, Edge>, HashSet<Node>>()
    private val pathsMap2 = hashMapOf<Pair<Edge, Edge>, Int>()
    private val pairsMap2 = hashMapOf<Pair<Edge, Edge>, HashSet<Pair<Node, Node>>>()
    private val oneMap2 = hashMapOf<Pair<Edge, Edge>, Int>()
    private val twoMap2 = hashMapOf<Pair<Edge, Edge>, Int>()

    fun out(edge: Edge): Int {
        return outMap[edge]?.size ?: 0
    }

    fun out(edges: Pair<Edge, Edge>): Int {
        return outMap2[edges]?.size ?: 0
    }

    fun `in`(edge: Edge): Int {
        return inMap[edge]?.size ?: 0
    }

    fun `in`(edges: Pair<Edge, Edge>): Int {
        return inMap2[edges]?.size ?: 0
    }

    fun middle(edges: Pair<Edge, Edge>): Int {
        return middleMap2[edges]?.size ?: 0
    }

    fun paths(edge: Edge): Int {
        return pathsMap[edge] ?: 0
    }

    fun paths(edges: Pair<Edge, Edge>): Int {
        return pathsMap2[edges] ?: 0
    }

    fun pairs(edge: Edge): Int {
        return pairsMap[edge]?.size ?: 0
    }

    fun pairs(edges: Pair<Edge, Edge>): Int {
        return pairsMap2[edges]?.size ?: 0
    }

    fun one(edges: Pair<Edge, Edge>): Int {
        return oneMap2[edges] ?: 0
    }

    fun two(edges: Pair<Edge, Edge>): Int {
        return twoMap2[edges] ?: 0
    }

    fun handleInsertion(path: Path) {

        val edges = PathIdentifierStore.getEdgeSet(path.pathId)

        if (edges.size == 1) {

            val edge = edges.first()
            val node1 = path.nodes.first()
            val node2 = path.nodes.last()

            // OUT
            outMap.compute(edge, { _, value -> value?.add(node1); value ?: HashSet(listOf(node1)) })

            // IN
            inMap.compute(edge, { _, value -> value?.add(node2); value ?: HashSet(listOf(node2)) })

            // PATH
            pathsMap.compute(edge, { _, value -> value?.inc() ?: 1 })

            // PAIR
            pairsMap.compute(edge, { _, value -> value?.add(Pair(node1, node2)); value ?: HashSet(listOf(Pair(node1, node2))) })

        }

        if (edges.size == 2) {

            val edge1 = edges.first()
            val edge2 = edges.last()
            val edgePair = Pair(edge1, edge2)

            val node1 = path.nodes.first()
            val node2 = path.nodes[1]
            val node3 = path.nodes.last()

            // OUT
            outMap2.compute(edgePair, { _, value -> value?.add(node1); value ?: HashSet(listOf(node1)) })

            // IN
            inMap2.compute(edgePair, { _, value -> value?.add(node3); value ?: HashSet(listOf(node3)) })

            // MIDDLE
            middleMap2.compute(edgePair, { _, value -> value?.add(node2); value ?: HashSet(listOf(node2)) })

            // PATH
            pathsMap2.compute(edgePair, { _, value -> value?.inc() ?: 1 })

            // PAIR
            pairsMap2.compute(edgePair, { _, value -> value?.add(Pair(node1, node3)); value ?: HashSet(listOf(Pair(node1, node3))) })

        }
    }

    fun afterMath() {

        pairsMap2.keys.forEach { edgePair ->

            // ONE
            oneMap2.compute(edgePair, { _, _ ->
                pairsMap[edgePair.first]!!
                        .filter { outMap2[edgePair]!!.contains(it.first) }
                        .filter { middleMap2[edgePair]!!.contains(it.second) }
                        .size
            })

            // TWO
            twoMap2.compute(edgePair, { _, _ ->
                pairsMap[edgePair.second]!!
                        .filter { middleMap2[edgePair]!!.contains(it.first) }
                        .filter { inMap2[edgePair]!!.contains(it.second) }
                        .size
            })
        }
    }
}