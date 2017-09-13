/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.graph

import com.google.common.collect.HashBiMap
import java.io.Serializable

/**
 * Data class for our Node model.
 *
 * See https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Node.java for the
 * implementation in PathDB.
 */
data class Node(val label: String) : Serializable {

    val id get() = getIdentifier(label)

    constructor(id: Long) : this(getLabel(id))

    companion object IdentifierStore {

        private val identifierMap = HashBiMap.create<Long, String>()
        private var maxId: Long = 0

        /**
         * Get or create an Edge ID for a given Edge.
         *
         * @param edge The Edge for which we want to generate or find an ID.
         * @return The ID generated for the given edge.
         */
        fun getIdentifier(label: String): Long {
            // Access the store or generate a key
            return identifierMap.inverse()[label] ?: generateIdentifier(label)
        }

        fun getLabel(id: Long): String {
            return identifierMap.getValue(id)
        }

        /**
         * This method is called when the given edgeLabel is not yet contained in the stores.
         * So it generates an ID and saves it to the stores.
         *
         * @param edgeLabel The edgeLabel for which we need to generate an ID
         * @return The ID generated for the given edgeLabel
         */
        private fun generateIdentifier(label: String): Long {
            identifierMap.put(maxId, label)
            return ++maxId - 1
        }

    }
}
