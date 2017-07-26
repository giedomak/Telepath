/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels

import java.io.Serializable
import java.util.*

/**
 * Path model
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Path.java
 */
class Path(pathId: Long, val nodes: List<Node>) : AbstractPath(pathId), Serializable {

    val length: Int get() = nodes.size

    init {
        // Validations
        if (nodes.size < 2)
            throw IllegalArgumentException("A Path must have at least two nodes")
    }

    // Return the first node of the nodes list
    fun firstNode(): Node {
        return nodes[0]
    }

    // Return the last node of the nodes list
    fun lastNode(): Node {
        return nodes[nodes.size - 1]
    }

    /**
     * --------- HASHCODE & EQUALS & TO-STRING ---------
     */

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
