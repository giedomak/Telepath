package com.telepathdb.datamodels;

/**
 * Node model
 * At the moment, we just extend Node from PathDB:
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Node.java
 */
public class Node extends com.pathdb.pathIndex.Node {
  public Node(long id) {
    super(id);
  }
}
