/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import com.telepathdb.datamodels.stores.PathIdentifierStore;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

/**
 * PathPrefix model At the moment, we just extend PathPrefix from PathDB:
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/PathPrefix.java
 */
public class PathPrefix extends AbstractPath {
  public final int length;
  public final List<Node> nodes;
  protected final int prefixLength;

  public PathPrefix(long pathId) {
    // We have to use numberOfEdges + 1, since PathDB uses the number of nodes.
    this(pathId, PathIdentifierStore.getEdgeSet(pathId).size() + 1);
  }

  private PathPrefix(long pathId, int length) {
    super(pathId);
    this.length = length;
    this.nodes = emptyList();
    this.prefixLength = 0;
  }

  private PathPrefix(long pathId, int length, List<Node> nodes) {
    super(pathId);
    this.length = length;
    this.nodes = nodes;
    this.prefixLength = nodes.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PathPrefix that = (PathPrefix) o;
    return pathId == that.pathId && length == that.length && Objects.equals(nodes, that.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pathId, length, nodes);
  }

  @Override
  public String toString() {
    return "PathPrefix{" + "pathId=" + pathId + ", length=" + length + ", edges=" + PathIdentifierStore.getEdgeSet(pathId) + ", nodes=" + nodes + "}";
  }
}
