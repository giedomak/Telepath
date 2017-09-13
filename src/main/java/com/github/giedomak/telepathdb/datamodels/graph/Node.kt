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
 * Data class for our node model.
 *
 * See https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Node.java for the
 * implementation in PathDB.
 *
 * @property label The label attached to this node.
 * @property id The identifier which maps to our label.
 */
data class Node(val label: String) : Serializable {

    val id get() = getIdentifier(label)

    constructor(id: Long) : this(getLabel(id))

    /**
     * Store which holds the mapping from labels to identifiers.
     */
    private companion object IdentifierStore {

        private val identifierMap = HashBiMap.create<Long, String>()
        private var maxId: Long = 0

        /**
         * Get or create an identifier for a given label.
         *
         * @param label The label for which we want to generate or find an identifier.
         * @return The identifier generated for the given label.
         */
        private fun getIdentifier(label: String): Long {
            // Access the store or generate a key
            return identifierMap.inverse()[label] ?: generateIdentifier(label)
        }

        /**
         * Get the label associated to the given identifier.
         *
         * @param id The identifier for which we want the label.
         * @return The label associated to the given identifier.
         */
        private fun getLabel(id: Long): String {
            return identifierMap.getValue(id)
        }

        /**
         * Generates an identifier and saves it to the [identifierMap].
         *
         * @param label The label for which we need to generate an identifier.
         * @return The identifier generated for the given label.
         */
        private fun generateIdentifier(label: String): Long {
            identifierMap.put(maxId, label)
            return ++maxId - 1
        }

    }
}
