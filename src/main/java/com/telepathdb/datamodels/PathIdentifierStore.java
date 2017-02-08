package com.telepathdb.datamodels;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

  static public long getPathIdentifierByEdgeLabel(String edgeLabel) {

    List edges = Arrays.asList(new Edge(Long.parseLong(edgeLabel)));
    return getPathIdentifierByEdgeSet(edges);
  }

  /**
   * Generate a path identifier for an edge set and add it to the store
   *
   * @param serialized Serialized list of edges of which to generate a Path identifier for
   * @return The generated path identifier
   */
  static private long generatePathIdentifier(String serialized) {
    pathIdentifierStore.put(serialized, maxId++);
    return maxId - 1;
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

    // Get the ids
    List<String> ids = new ArrayList<String>();
    for (Edge edge : edges) {
      ids.add(Long.toString(edge.getId()));
    }

    // Return the joined string with a separator
    return StringUtils.join(ids, ",");
  }

}
