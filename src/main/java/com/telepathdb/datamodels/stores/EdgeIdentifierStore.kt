/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.stores

import com.telepathdb.datamodels.Edge
import java.util.*

/**
 * This class maps edge labels to ids and stores them in a hashmap.
 */
object EdgeIdentifierStore {

    private val edgeLabelStore = HashMap<String, Long>()
    private val edgeIdentifierStore = HashMap<Long, String>()
    private var maxId: Long = 1

    /**
     * Get or create an Edge ID for a given Edge.

     * @param edge The Edge for which we want to generate or find an ID.
     * @return The ID generated for the given edge
     */
    fun getEdgeIdentifier(edge: Edge): Long {
        // Access the store or generate a key
        if (edgeLabelStore.containsKey(edge.label)) {
            return edgeLabelStore[edge.label] ?: -1
        } else {
            return generateEdgeIdentifier(edge.label)
        }
    }

    /**
     * Return the Edge when given an edgeId
     *
     * @param edgeId The EdgeID for which we want to find the Edge
     * @return An Edge object associated with the given edgeId
     */
    fun getEdge(edgeId: Long): Edge {
        // Lookup the edgeId
        if (edgeIdentifierStore.containsKey(edgeId)) {
            return Edge(edgeIdentifierStore[edgeId] ?: "")
        } else {
            throw IllegalArgumentException("EdgeIdentifierStore: edgeId not known")
        }
    }

    /**
     * This method is called when the given edgeLabel is not yet contained in the stores.
     * So it generates an ID and saves it to the stores.

     * @param edgeLabel The edgeLabel for which we need to generate an ID
     * @return The ID generated for the given edgeLabel
     */
    private fun generateEdgeIdentifier(edgeLabel: String): Long {
        edgeLabelStore.put(edgeLabel, maxId)
        edgeIdentifierStore.put(maxId, edgeLabel)
        return ++maxId - 1
    }

}
