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
data class Path(val pathId: Long, var nodes: List<Node>) : AbstractPath(pathId), Serializable {

    init {
        // Validations
        if (nodes.size < 2)
            throw IllegalArgumentException("A Path must have at least two nodes")
    }

    // Return the length of this path, i.e. the number of nodes
    fun length(): Int = nodes.size

    // Return the first node of the nodes list
    fun firstNode(): Node {
        return nodes[0]
    }

    // Return the last node of the nodes list
    fun lastNode(): Node {
        return nodes[nodes.size - 1]
    }

    // We want control over our own hashcode
    override fun hashCode(): Int {
        return Objects.hash(length(), nodes)
    }
}
