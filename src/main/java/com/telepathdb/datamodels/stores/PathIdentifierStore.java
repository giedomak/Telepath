/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.stores;

import com.telepathdb.datamodels.Edge;
import com.telepathdb.datamodels.Node;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathPrefix;
import com.telepathdb.datamodels.utilities.Logger;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create a Path with its identifier from a set of edges and store them in a hashmap
 */
final public class PathIdentifierStore {

  static public HashMap<Long, String> pathIdentifierStore = new HashMap<Long, String>();
  // Let's say we have 3 edges along a path with EdgeIds 3, 6 and 33.
  // We will get the String "3,6,33" as the key for this hashmap for that path.
  static private HashMap<String, Long> pathEdgeSerializationStore = new HashMap<String, Long>();
  // key: k, value: List of pathIdentifiers which have k number of edges
  static private HashMap<Integer, List<Long>> kPathIdentifierStore = new HashMap<Integer, List<Long>>();

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
      return generatePathIdentifier(edges);
    }
  }

  /**
   * Proxy method for getPathIdentifierByEdgeSet when only one edgeLabel is given.
   *
   * @param edgeLabel The edgeLabel for which we create an Edge.
   * @return The Path identifier created or found for the given edge
   */
  static public long getPathIdentifierByEdgeLabel(String edgeLabel) {
    return getPathIdentifierByEdgeLabel(Arrays.asList(edgeLabel));
  }

  static public long getPathIdentifierByEdgeLabel(List<String> edgeLabels) {
    List<Edge> edges = edgeLabels.stream().map(Edge::new).collect(Collectors.toList());
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
      throw new IllegalArgumentException("PathIdentifierStore: pathIdentifier not known");
    }
  }

  static public Path concatenatePathsAndStore(Path path1, Path path2) {
    List<Edge> edges = ListUtils.union(getEdgeSet(path1.getPathId()), getEdgeSet(path2.getPathId()));
    List<Node> sliced = new ArrayList<Node>((List<Node>) (List<?>) path2.getNodes()); // Some typecasting

    // Remove the first node from the second path, otherwise we have a duplicate
    sliced.remove(0);

    List<Node> nodes = ListUtils.union(path1.getNodes(), sliced);
    return new Path(getPathIdentifierByEdgeSet(edges), nodes);
  }

  /**
   * Get all path identifiers which have size k.
   *
   * @param k The size of the paths we are looking for.
   * @return List of path identifiers of size k.
   */
  static public List<Long> getPathIdentifiers(int k) {
    return kPathIdentifierStore.getOrDefault(k, Collections.emptyList());
  }

  /**
   * Generate a path identifier for an edge set and add it to the store
   *
   * @param edges List of edges of which to generate a Path identifier for
   * @return The generated path identifier
   */
  static private long generatePathIdentifier(List<Edge> edges) {

    // Serialize the edge set
    String serialized = serializeEdgeSet(edges);

    // Add to the stores
    pathEdgeSerializationStore.put(serialized, maxId);
    pathIdentifierStore.put(maxId, serialized);

    // Add to the k-store
    List<Long> ids = kPathIdentifierStore.getOrDefault(edges.size(), new ArrayList<Long>());
    ids.add(maxId);
    kPathIdentifierStore.put(edges.size(), ids);

    // Print the addition
    Logger.debug("Add: " + new PathPrefix(maxId));

    // Increase maxId, but return the id we've just used for generation
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
    return StringUtils.join(ids, ";");
  }

  /**
   * Deserialize a serializedEdgeSet String back to a List of Edges.
   *
   * @param serializedEdgeSet The serialized String for an Edge Set.
   * @return A list of edges belonging to the serialized String.
   */
  static private List<Edge> deserializeEdgeSet(String serializedEdgeSet) {
    String[] edgeIds = serializedEdgeSet.split(";");
    List<Edge> edges = new ArrayList<>();
    for (String edgeId : edgeIds) {
      edges.add(EdgeIdentifierStore.getEdge(Long.parseLong(edgeId)));
    }
    return edges;
  }
}
