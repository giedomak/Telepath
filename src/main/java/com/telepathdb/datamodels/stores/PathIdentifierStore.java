/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.stores;

import com.telepathdb.datamodels.Edge;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Create a Path with its identifier from a set of edges and store them in a hashmap
 */
final public class PathIdentifierStore {

  // Let's say we have 3 edges along a path with EdgeIds 3, 6 and 33.
  // We will get the String "3,6,33" as the key for this hashmap for that path.
  static private HashMap<String, Long> pathEdgeSerializationStore = new HashMap<>();
  static public HashMap<Long, String> pathIdentifierStore = new HashMap<>();
  static private long maxId = 1;

  private PathIdentifierStore() {
  }

  /**
   * Get the Path identifier which belongs to a certain edge set.
   *
   * @param edges The edge set for which to retrieve the path identifier
   * @return The path identifier
   */
  static public long getPathIdentifierByEdgeSet(List<Edge> edges) {

    // Serialize the edge set
    String serialized = serializeEdgeSet(edges);

    // Access the store or generate a key
    if (pathEdgeSerializationStore.containsKey(serialized)) {
      return pathEdgeSerializationStore.get(serialized);
    } else {
      return generatePathIdentifier(serialized);
    }
  }

  /**
   * Proxy method for getPathIdentifierByEdgeSet when only one edgeLabel is given.
   *
   * @param edgeLabel The edgeLabel for which we create an Edge.
   * @return The Path identifier created or found for the given edge
   */
  static public long getPathIdentifierByEdgeLabel(String edgeLabel) {
    List edges = Arrays.asList(new Edge(edgeLabel));
    return getPathIdentifierByEdgeSet(edges);
  }

  /**
   * Return a list of Edges for a given path identifier.
   *
   * @param pathIdentifier The path identifier for which to gather the list of containing Edges.
   * @return A list of Edges belonging to a certain path identifier.
   */
  static public List<Edge> getEdgeSet(long pathIdentifier) {
    if (pathIdentifierStore.containsKey(pathIdentifier)) {
      return deserializeEdgeSet(pathIdentifierStore.get(pathIdentifier));
    } else {
      throw new IllegalArgumentException("Path ID not known");
    }
  }

  /**
   * Generate a path identifier for an edge set and add it to the store
   *
   * @param serialized Serialized list of edges of which to generate a Path identifier for
   * @return The generated path identifier
   */
  static private long generatePathIdentifier(String serialized) {
    pathEdgeSerializationStore.put(serialized, maxId);
    pathIdentifierStore.put(maxId, serialized);
    return ++maxId - 1;
  }

  /**
   * Serialize a list of edges into a String
   * Let's say we have 3 edges with ids 3, 6 and 33.
   * We will get the String "3,6,33"
   *
   * @param edges List of edges to serialize
   * @return Serialized String
   */
  static private String serializeEdgeSet(List<Edge> edges) {

    // Get the ids as string
    List<String> ids = new ArrayList<String>();
    for (Edge edge : edges) {
      Long edgeId = EdgeIdentifierStore.getEdgeIdentifier(edge);
      ids.add(Long.toString(edgeId));
    }

    // Return the joined string with a separator
    return StringUtils.join(ids, ",");
  }

  /**
   * Deserialize a serializedEdgeSet String back to a List of Edges.
   *
   * @param serializedEdgeSet The serialized String for an Edge Set.
   * @return A list of edges belonging to the serialized String.
   */
  static private List<Edge> deserializeEdgeSet(String serializedEdgeSet) {
    String[] edgeIds = serializedEdgeSet.split(",");
    List<Edge> edges = new ArrayList<>();
    for (String edgeId : edgeIds) {
      edges.add(EdgeIdentifierStore.getEdge(Long.parseLong(edgeId)));
    }
    return edges;
  }

}
