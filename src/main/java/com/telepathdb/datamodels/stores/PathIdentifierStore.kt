/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.stores

import com.telepathdb.datamodels.Edge
import com.telepathdb.datamodels.Node
import com.telepathdb.datamodels.Path
import com.telepathdb.datamodels.PathPrefix
import com.telepathdb.datamodels.utilities.Logger
import org.apache.commons.collections.ListUtils
import org.apache.commons.lang3.StringUtils
import java.util.stream.Collectors

/**
 * Create a Path with its identifier from a set of edges and store them in a hashmap.
 */
object PathIdentifierStore {

    val pathIdentifierStore = hashMapOf<Long, String>()
    // Let's say we have 3 edges along a path with EdgeIds 3, 6 and 33.
    // We will get the String "3,6,33" as the key for this hashmap for that path.
    private val pathEdgeSerializationStore = hashMapOf<String, Long>()
    // key: k, value: List of pathIdentifiers which have k number of edges
    private val kPathIdentifierStore = mutableMapOf<Int, List<Long>>()
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
        if (pathEdgeSerializationStore.containsKey(serialized)) {
            return pathEdgeSerializationStore[serialized] ?: -1
        } else {
            return generatePathId(edges)
        }
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
        val sliced = ArrayList(path2.nodes)

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
        val pathIds = kPathIdentifierStore[edges.size] ?: ArrayList<Long>()
        kPathIdentifierStore.put(edges.size, pathIds + maxId)

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
        val ids = ArrayList<String>()
        for (edge in edges) {
            val edgeId = EdgeIdentifierStore.getEdgeIdentifier(edge)
            ids.add(java.lang.Long.toString(edgeId))
        }

        // Return the joined string with a separator
        return StringUtils.join(ids, ";")
    }

    /**
     * Deserialize a serializedEdgeSet String back to a List of Edges.
     *
     * @param serializedEdgeSet The serialized String for an Edge Set.
     * @return A list of edges belonging to the serialized String.
     */
    private fun deserializeEdgeSet(serializedEdgeSet: String): List<Edge> {
        val edgeIds = serializedEdgeSet.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val edges = ArrayList<Edge>()
        for (edgeId in edgeIds) {
            edges.add(EdgeIdentifierStore.getEdge(java.lang.Long.parseLong(edgeId)))
        }
        return edges
    }
}
