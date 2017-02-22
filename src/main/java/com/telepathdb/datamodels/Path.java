/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Path model
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Path.java
 */
public class Path extends AbstractPath implements Serializable {

  public final int length;
  public List<Node> nodes;
  private final int numberOfEdges;

  public Path(long pathId, List<Node> nodes) {
    super(pathId);
    this.length = nodes.size();
    this.nodes = nodes;

    if (nodes.size() < 2)
      throw new IllegalArgumentException("A Path must have at least two nodes");

    this.numberOfEdges = nodes.size() - 1;

  }

  public Node lastNode() {
    return (Node) nodes.get(nodes.size() - 1);
  }

  public Node firstNode() {
    return (Node) nodes.get(0);
  }

  public static byte[] serialize(List<Path> paths) {
    return SerializationUtils.serialize((Serializable) paths);
  }

  public static List<Path> deserialize(byte[] data) {
    return (List<Path>) SerializationUtils.deserialize(data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Path that = (Path) o;
    return pathId == that.pathId &&
        length == that.length &&
        Objects.equals(nodes, that.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(length, nodes);
  }

  @Override
  public String toString() {
    return "Path{" + "pathId=" + pathId + ", length=" + length + ", nodes=" + nodes + "}\n";
  }
}