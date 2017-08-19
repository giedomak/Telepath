/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import java.io.Serializable
import java.util.*

/**
 * The Path model contains a list of edges, as well as a list of nodes to represent a specific path.
 *
 * A path is basically an ordered set of nodes and edges we encounter along a path through the graph.
 * Let's say we've got this path: `Tim - likes - Stef - loves - Bregt`.
 * Tim, Stef and Bregt are [nodes][Node], likes and loves are [edge labels][Edge].
 *
 * Our Path data model has two properties to maintain the nodes we encounter, and the edges we encounter.
 * The list of edges is hidden inside the [PathIdentifierStore][com.telepathdb.datamodels.stores.PathIdentifierStore], where we can
 * retrieve the list of edges by using our [pathId].
 *
 * In our example, our path instance holds a [pathId] which points to a list with `['likes', 'loves']`. Our path instance
 * holds a list of [nodes] with `['Tim', 'Stef', 'Bregt']` for simplicity.
 *
 * See https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Path.java for the
 * Path data class used in PathDB.
 *
 * @property pathId The ID given to a specific Path. We can use the [PathIdentifierStore][com.telepathdb.datamodels.stores.PathIdentifierStore]
 * to get the list of [edges][Edge].
 * @property nodes The ordered list of nodes along this [Path].
 * @property length Getter for the size of our [nodes] list.
 * @constructor Creates a Path with an ID and a list of nodes. The list of nodes should have at least two nodes.
 */
class Path(pathId: Long, val nodes: List<Node>) : AbstractPath(pathId), Serializable {

    val length: Int get() = nodes.size

    init {
        // Validations
        if (nodes.size < 2)
            throw IllegalArgumentException("A Path must have at least two nodes")
    }

    //
    // --------- HASHCODE & EQUALS & TO-STRING ---------
    //

    override fun hashCode(): Int {
        return Objects.hash(length, nodes)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Path

        if (nodes != other.nodes) return false

        return true
    }

    override fun toString(): String {
        return "Path(pathId=$pathId, nodes=$nodes)"
    }
}
