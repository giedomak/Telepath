package com.telepathdb.datamodels;

import java.util.List;

/**
 * PathPrefix model
 */
public class PathPrefix extends com.pathdb.pathIndex.PathPrefix {
  public PathPrefix(long pathId, int length) {
    super(pathId, length);
  }

  public PathPrefix(long pathId, int length, List<Node> nodes) {
    // Convert our own Node model back to the Node model from PathDB
    super(pathId, length, (List<com.pathdb.pathIndex.Node>) (List<?>) nodes);
  }
}
