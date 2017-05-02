/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.stores;

import com.telepathdb.datamodels.Edge;

import java.util.HashMap;

/**
 * This class maps edge labels to ids and stores them in a hashmap.
 */
final public class EdgeIdentifierStore {

  static private HashMap<String, Long> edgeLabelStore = new HashMap<>();
  static private HashMap<Long, String> edgeIdentifierStore = new HashMap<>();
  static private long maxId = 1;

  /**
   * Get or create an Edge ID for a given Edge.
   *
   * @param edge The Edge for which we want to generate or find an ID.
   * @return The ID generated for the given edge
   */
  static public long getEdgeIdentifier(Edge edge) {

    // Access the store or generate a key
    if (edgeLabelStore.containsKey(edge.getLabel())) {
      return edgeLabelStore.get(edge.getLabel());
    } else {
      return generateEdgeIdentifier(edge.getLabel());
    }
  }

  /**
   * Get or create an Edge ID for a given edge label.
   *
   * @param edgeLabel The edge label for which we want to generate or find an ID.
   * @return The ID generated for the given edge.
   */
  static public long getEdgeIdentifier(String edgeLabel) {
    return getEdgeIdentifier(new Edge(edgeLabel));
  }

  /**
   * Return the Edge when given an edgeId
   *
   * @param edgeId The EdgeID for which we want to find the Edge
   * @return An Edge object associated with the given edgeId
   */
  static public Edge getEdge(long edgeId) {

    // Lookup the edgeId
    if (edgeIdentifierStore.containsKey(edgeId)) {
      return new Edge(edgeIdentifierStore.get(edgeId));
    } else {
      throw new IllegalArgumentException("EdgeIdentifierStore: edgeId not known");
    }
  }

  /**
   * This method is called when the given edgeLabel is not yet contained in the stores.
   * So it generates an ID and saves it to the stores.
   *
   * @param edgeLabel The edgeLabel for which we need to generate an ID
   * @return The ID generated for the given edgeLabel
   */
  static private long generateEdgeIdentifier(String edgeLabel) {
    edgeLabelStore.put(edgeLabel, maxId);
    edgeIdentifierStore.put(maxId, edgeLabel);
    return ++maxId - 1;
  }

}
