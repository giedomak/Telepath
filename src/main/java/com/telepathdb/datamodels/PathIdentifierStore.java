package com.telepathdb.datamodels;

import java.util.HashMap;
import java.util.List;

/**
 * Create a Path with its identifier from a set of edges and store them in a hashmap
 */
final public class PathIdentifierStore {

  static public HashMap<String, Long> pathIdentifierStore = new HashMap<>();
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
    if (pathIdentifierStore.containsKey(serialized)) {
      return pathIdentifierStore.get(serialized);
    } else {
      return generatePathIdentifier(serialized);
    }
  }

  /**
   * Generate a path identifier for an edge set and add it to the store
   *
   * @param edges List of edges of which to generate an Path identifier for
   * @return The generated path identifier
   */
  static private long generatePathIdentifier(String edges) {
    pathIdentifierStore.put(edges, maxId++);
    return maxId - 1;
  }

  static private String serializeEdgeSet(List<Edge> edges) {

    // Initialize
    String serializedEdgeIds = "";

    // Fill the String
    for (Edge edge : edges) {
      serializedEdgeIds += edge.getId() + ",";
    }

    return serializedEdgeIds;
  }

}
