/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.datamodels.graph

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
 * @property pathId The ID given to a specific Path. We can use the [PathIdentifierStore][com.telepath.datamodels.stores.PathIdentifierStore]
 * to get the list of [edges][Edge].
 * @property nodes Ordered list of nodes along the path we want to restrict our query with.
 * @constructor Creates a PathPrefix with only the list of edges known through the [pathId].
 */
data class PathPrefix(
        val pathId: Long,
        val nodes: List<Node> = emptyList<Node>()
)
