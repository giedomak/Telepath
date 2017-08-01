/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.stores

import com.github.giedomak.telepathdb.datamodels.Edge
import com.github.giedomak.telepathdb.datamodels.Node
import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore.kPathIdentifierStore
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore.maxId
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore.pathEdgeSerializationStore
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore.pathIdentifierStore
import com.github.giedomak.telepathdb.datamodels.utilities.Logger
import org.apache.commons.collections.ListUtils
import org.apache.commons.lang3.StringUtils
import java.util.stream.Collectors

/**
 * Singleton class which stores the mapping from a list of edges to IDs.
 *
 * Let's say we have 3 edges along a path with edge IDs 6, 3 and 33.
 * We serialize these edge IDs into the String `"6,3,33"`.
 * This serialized String will be the key in our [pathEdgeSerializationStore] HashMap. And it will be the value
 * in our [pathIdentifierStore] HashMap. We map this String to a generated ID.
 *
 * @property pathIdentifierStore HashMap which maps path IDs to serialized Strings of edge IDs.
 * @property pathEdgeSerializationStore HashMap which maps serialized Strings of edge IDs to path IDs.
 * @property kPathIdentifierStore HashMap which has key: k, value: List of path IDs which have k number of edges.
 * @property maxId Counter to keep track of the maximum path ID we have stored so far.
 */
object PathIdentifierStore {

    private val pathIdentifierStore = hashMapOf<Long, String>()
    private val pathEdgeSerializationStore = hashMapOf<String, Long>()
    private val kPathIdentifierStore = hashMapOf<Int, MutableList<Long>>()
    private var maxId: Long = 1

    /**
     * Get the Path identifier which belongs to a certain edge set.
     *
     * @param edges The edge set for which to retrieve the path identifier
     * @return The path identifier
     */
    fun getPathIdByEdges(edges: List<Edge>): Long {
        // Serialize the edge set
        val serialized = serializeEdgeSet(edges)

        // Access the store or generate a key
        return pathEdgeSerializationStore[serialized] ?: generatePathId(edges)
    }

    /**
     * Get or generate a pathId when given a single edge label.
     */
    fun getPathIdByEdgeLabel(edgeLabel: String): Long {
        return getPathIdByEdgeLabel(listOf(edgeLabel))
    }

    /**
     * Get or generate a pathId for a given list of edge labels.
     *
     * @param edgeLabels List of edge labels for which we lookup the pathId.
     * @return The pathId we've found or generated for the given edge labels.
     */
    fun getPathIdByEdgeLabel(edgeLabels: List<String>): Long {
        // Map the edge labels into Edges
        val edges = edgeLabels.stream()
                .map { Edge(it) }
                .collect(Collectors.toList())

        return getPathIdByEdges(edges)
    }

    /**
     * Return a list of Edges for a given path identifier.
     *
     * @param pathIdentifier The path identifier for which to gather the list of containing Edges.
     * @return A list of Edges belonging to a certain path identifier.
     */
    fun getEdgeSet(pathIdentifier: Long): List<Edge> {
        if (pathIdentifierStore.containsKey(pathIdentifier)) {
            return deserializeEdgeSet(pathIdentifierStore[pathIdentifier] ?: "")
        } else {
            throw IllegalArgumentException("PathIdentifierStore: pathIdentifier not known")
        }
    }

    /**
     * Concatenate two given paths.
     *
     * Example: Let's say we've got two paths: `Luuk - worksFor - Drieam` and `Drieam - earns - Money`.
     * So our result should be: `Luuk - worksFor - Drieam - earns - Money`.
     * We have to strip out once occurrence of `Drieam`, otherwise we'll have a duplicate [Node].
     *
     * @param path1 The first part of the to-be concatenated path.
     * @param path2 The last part of the to-be concatenated path.
     * @return The concatenation of the two given paths.
     */
    fun concatenatePaths(path1: Path, path2: Path): Path {
        // Perform the union on both paths
        @Suppress("UNCHECKED_CAST")
        val edges = ListUtils.union(
                getEdgeSet(path1.pathId), getEdgeSet(path2.pathId)
        ) as List<Edge>

        // Get the nodes of the second path
        val sliced = mutableListOf(path2.nodes)

        // Remove the first node from the second path, otherwise we have a duplicate
        sliced.removeAt(0)

        // Perform union on the nodes from path1 and the sliced nodes from path2.
        @Suppress("UNCHECKED_CAST")
        val nodes = ListUtils.union(path1.nodes, sliced) as List<Node>

        return Path(getPathIdByEdges(edges), nodes)
    }

    /**
     * Get all path identifiers which have size k.
     *
     * @param k The size of the paths we are looking for.
     * @return List of path identifiers of size k.
     */
    fun getPathIds(k: Int): List<Long> {
        return kPathIdentifierStore.getOrDefault(k, emptyList<Long>())
    }

    /**
     * Generate a path identifier for an edge set and add it to the store
     *
     * @param edges List of edges of which to generate a Path identifier for
     * @return The generated path identifier
     */
    private fun generatePathId(edges: List<Edge>): Long {
        // Serialize the edge set
        val serialized = serializeEdgeSet(edges)

        // Add to the stores
        pathEdgeSerializationStore.put(serialized, maxId)
        pathIdentifierStore.put(maxId, serialized)

        // Add to the k-store
        val pathIds = kPathIdentifierStore.getOrPut(edges.size, { mutableListOf() })
        pathIds.add(maxId)

        // Print the addition
        Logger.debug("Add: " + PathPrefix(maxId))

        // Increase maxId, but return the id we've just used for generation
        return ++maxId - 1
    }

    /**
     * Serialize a list of edges into a String.
     *
     * Let's say we have 3 edges with ids 3, 6 and 33.
     * We will get the String `"3,6,33"`
     *
     * @param edges List of edges to serialize
     * @return Serialized String
     */
    private fun serializeEdgeSet(edges: List<Edge>): String {
        // Get the ids as string
        val edgeIds = edges.map { EdgeIdentifierStore.getEdgeIdentifier(it).toString() }

        // Return the joined string with a separator
        return StringUtils.join(edgeIds, ";")
    }

    /**
     * Deserialize a serializedEdgeSet String back to a List of Edges.
     *
     * @param serializedEdgeSet The serialized String for an Edge Set.
     * @return A list of edges belonging to the serialized String.
     */
    private fun deserializeEdgeSet(serializedEdgeSet: String): List<Edge> {
        val edgeIds = serializedEdgeSet.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return edgeIds.map { EdgeIdentifierStore.getEdge(it.toLong()) }
    }
}
