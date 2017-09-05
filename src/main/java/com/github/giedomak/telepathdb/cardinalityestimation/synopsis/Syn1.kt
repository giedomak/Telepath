package com.github.giedomak.telepathdb.cardinalityestimation.synopsis

import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore

class Syn1 {

    val outMap = hashMapOf<Edge, HashSet<Node>>()
    val inMap = hashMapOf<Edge, HashSet<Node>>()
    val pathsMap = hashMapOf<Edge, Int>()
    val pairsMap = hashMapOf<Edge, HashSet<Pair<Node, Node>>>()

    fun handleInsertion(path: Path) {

        // Get the list of edges associated with the path and grab the first.
        val edge = PathIdentifierStore.getEdgeSet(path.pathId).first()

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
}