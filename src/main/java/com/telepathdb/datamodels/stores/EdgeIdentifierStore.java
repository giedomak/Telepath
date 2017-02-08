package com.telepathdb.datamodels.stores;

import com.telepathdb.datamodels.Edge;

import java.util.HashMap;

/**
 * Created by giedomak on 08/02/2017.
 */
final public class EdgeIdentifierStore {

  static private HashMap<String, Long> edgeLabelStore = new HashMap<>();
  static private HashMap<Long, String> edgeIdentifierStore = new HashMap<>();
  static private long maxId = 1;

  static public long getEdgeIdentifier(Edge edge) {

    // Access the store or generate a key
    if (edgeLabelStore.containsKey(edge.getLabel())) {
      return edgeLabelStore.get(edge.getLabel());
    } else {
      return generateEdgeIdentifier(edge.getLabel());
    }
  }

  static public Edge getEdge(long edgeId) {

    // Lookup the edgeId
    if (edgeIdentifierStore.containsKey(edgeId)) {
      return new Edge(edgeIdentifierStore.get(edgeId));
    } else {
      throw new IllegalArgumentException("Edge ID not known");
    }
  }

  static private long generateEdgeIdentifier(String edgeLabel) {
    edgeLabelStore.put(edgeLabel, maxId);
    edgeIdentifierStore.put(maxId, edgeLabel);
    return ++maxId - 1;
  }

}
