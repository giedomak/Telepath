package com.telepathdb.datamodels;

import java.util.List;

/**
 * Path model
 */
public class Path extends com.pathdb.pathIndex.Path {
  public Path(long pathId, List<Node> nodes) {
    // Convert our own Node model back to the Node model from PathDB
    super(pathId, (List<com.pathdb.pathIndex.Node>) (List<?>) nodes);
  }
}
