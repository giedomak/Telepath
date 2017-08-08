/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.cardinalityestimation.CardinalityEstimation
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import java.util.*

/**
 * This model is used for defining a search-query.
 *
 * It needs at least a [pathId] to know the edge labels along the path we are querying. It also has a constructor
 * where nodes can be given as params. These [nodes] will be a prefix of the path, and don't need to have the same
 * length as the edges plus one.
 *
 * Example: `Luuk - knows - Giedo - drinksWith - Schaap`.
 * We have a [pathId] which corresponds to the array `["knows", "drinksWith"]`.
 * If our pathPrefix only has this [pathId], it will query for all tuples which are connected through these two edges.
 * When we have `["Luuk"]` as the [nodes] property. It will query only for tuples which have `"Luuk"` as its first element.
 *
 * See https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/PathPrefix.java for
 * the implementation in PathDB.
 *
 * @property pathId The ID given to a specific Path. We can use the [PathIdentifierStore][com.telepathdb.datamodels.stores.PathIdentifierStore]
 * to get the list of [edges][Edge].
 * @property length The length of our path, i.e. the number of edges + 1.
 * @property nodes Ordered list of nodes along the path we want to restrict our query with.
 * @property prefixLength Getter for the size of our [nodes] list.
 * @property cardinality Get the cardinality of this [pathId] from the path index.
 * @constructor Creates a PathPrefix with only the list of edges known through the [pathId].
 */
class PathPrefix(pathId: Long) : AbstractPath(pathId) {

    var length = 0
    var nodes = emptyList<Node>()
    val prefixLength get() = nodes.size
    val cardinality get() = CardinalityEstimation.getCardinality(pathId)

    /**
     * @constructor Creates a PathPrefix where we also want to restrict our query with a [nodes] list.
     * @param pathId The ID representing the edge labels along a path.
     * @param length The length of the path, i.e. the number of edges + 1.
     * @param nodes List of nodes restricting our query to.
     */
    constructor(pathId: Long, length: Int, nodes: List<Node>) : this(pathId) {
        this.length = length
        this.nodes = nodes
    }

    //
    // --------- HASHCODE & EQUALS & TO-STRING ---------
    //

    override fun hashCode(): Int {
        return Objects.hash(pathId, length, nodes)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as PathPrefix

        if (pathId != other.pathId) return false
        if (length != other.length) return false
        if (nodes != other.nodes) return false

        return true
    }

    override fun toString(): String {
        return "PathPrefix(pathId=$pathId, length=$length, edges=" + PathIdentifierStore.getEdgeSet(pathId) + ", nodes=$nodes)"
    }
}
