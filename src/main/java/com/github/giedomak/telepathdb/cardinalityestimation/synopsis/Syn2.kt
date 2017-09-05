package com.github.giedomak.telepathdb.cardinalityestimation.synopsis

import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore

class Syn2 {

    val outMap = hashMapOf<Pair<Edge, Edge>, HashSet<Node>>()
    val inMap = hashMapOf<Pair<Edge, Edge>, HashSet<Node>>()
    val middleMap = hashMapOf<Pair<Edge, Edge>, HashSet<Node>>()
    val pathsMap = hashMapOf<Pair<Edge, Edge>, Int>()
    val pairsMap = hashMapOf<Pair<Edge, Edge>, HashSet<Pair<Node, Node>>>()
    val oneMap = hashMapOf<Pair<Edge, Edge>, Int>()
    val twoMap = hashMapOf<Pair<Edge, Edge>, Int>()

    fun handleInsertion(path: Path) {

        // Get the list of edges associated with the path and create the pair.
        val edges = PathIdentifierStore.getEdgeSet(path.pathId)
        val edgePair = Pair(edges.first(), edges.last())

        val node1 = path.nodes.first()
        val node2 = path.nodes[1]
        val node3 = path.nodes.last()

        // OUT
        outMap.compute(edgePair, { _, value -> value?.add(node1); value ?: HashSet(listOf(node1)) })

        // IN
        inMap.compute(edgePair, { _, value -> value?.add(node3); value ?: HashSet(listOf(node3)) })

        // MIDDLE
        middleMap.compute(edgePair, { _, value -> value?.add(node2); value ?: HashSet(listOf(node2)) })

        // PATH
        pathsMap.compute(edgePair, { _, value -> value?.inc() ?: 1 })

        // PAIR
        pairsMap.compute(edgePair, { _, value -> value?.add(Pair(node1, node3)); value ?: HashSet(listOf(Pair(node1, node3))) })

    }
}