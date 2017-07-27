/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels

import com.telepathdb.datamodels.stores.PathIdentifierStore
import java.util.*
import java.util.Collections.emptyList

/**
 * PathPrefix model At the moment, we just extend PathPrefix from PathDB:
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/PathPrefix.java
 */
class PathPrefix(pathId: Long) : AbstractPath(pathId) {

    var length: Int = 0
    var nodes: List<Node> = emptyList<Node>()
    val prefixLength: Int get() = nodes.size

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

        if (length != other.length) return false
        if (nodes != other.nodes) return false

        return true
    }

    override fun toString(): String {
        return "PathPrefix(pathId=$pathId, length=$length, edges=" + PathIdentifierStore.getEdgeSet(pathId) + ", nodes=$nodes)"
    }

}
