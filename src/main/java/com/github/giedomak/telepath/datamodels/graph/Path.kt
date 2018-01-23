/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.datamodels.graph

import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import java.io.Serializable

/**
 * The Path model contains a list of edges, as well as a list of nodes to represent a specific path.
 *
 * A path is basically an ordered set of nodes and edges we encounter along a path through the graph.
 * Let's say we've got this path: `Tim - likes - Stef - loves - Bregt`.
 * Tim, Stef and Bregt are [nodes][Node], likes and loves are [edge labels][Edge].
 *
 * Our Path data model has two properties to maintain the nodes we encounter, and the edges we encounter.
 * The list of edges is hidden inside the [PathIdentifierStore][com.telepath.datamodels.stores.PathIdentifierStore], where we can
 * retrieve the list of edges by using our [pathId].
 *
 * In our example, our path instance holds a [pathId] which points to a list with `['likes', 'loves']`. Our path instance
 * holds a list of [nodes] with `['Tim', 'Stef', 'Bregt']` for simplicity.
 *
 * See https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Path.java for the
 * Path data class used in PathDB.
 *
 * @property pathId The ID given to a specific Path. We can use the [PathIdentifierStore][com.telepath.datamodels.stores.PathIdentifierStore]
 * to get the list of [edges][Edge].
 * @property nodes The ordered list of nodes along this [Path].
 * @constructor Creates a Path with an ID and a list of nodes. The list of nodes should have at least two nodes.
 */
data class Path(val pathId: Long, val nodes: List<Node>) : Serializable, Comparable<Path> {

    private val length get() = nodes.size - 1

    override fun compareTo(other: Path): Int {

        if (this == other) return 0
        if (this.pathId != other.pathId) return if (this.pathId > other.pathId) 1 else -1
        if (this.length != other.length) return this.length - other.length

        for (i in nodes.indices) {
            if (this.nodes[i].id != other.nodes[i].id) {
                return java.lang.Long.compare(this.nodes[i].id, other.nodes[i].id)
            }
        }

        return 0
    }

    init {
        // Validations
        if (nodes.size < 2)
            throw IllegalArgumentException("A Path must have at least two nodes")
    }

    fun inverse(): Path {
        val edges = PathIdentifierStore.getEdgeSet(pathId).map { it.inverse() }
        val newId = PathIdentifierStore.getPathIdByEdges(edges.reversed())
        return Path(newId, nodes.reversed())
    }

}
