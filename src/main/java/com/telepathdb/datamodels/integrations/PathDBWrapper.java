/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.integrations;

import com.pathdb.pathIndex.Node;
import com.pathdb.pathIndex.Path;
import com.pathdb.pathIndex.PathPrefix;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by giedomak on 22/02/2017.
 */
public final class PathDBWrapper {

  public static Path toPath(com.telepathdb.datamodels.Path path) {
    return new Path(path.pathId, toNodeList(path.nodes));
  }

  public static com.telepathdb.datamodels.Path fromPath(Path path) {
    return new com.telepathdb.datamodels.Path(path.pathId, fromNodeList(path.nodes));
  }

  public static PathPrefix toPathPrefix(com.telepathdb.datamodels.PathPrefix pathPrefix) {
    return new PathPrefix(pathPrefix.pathId, pathPrefix.length, toNodeList(pathPrefix.nodes));
  }

  public static Node toNode(com.telepathdb.datamodels.Node node) {
    return new Node(node.getId());
  }

  public static com.telepathdb.datamodels.Node fromNode(Node node) {
    return new com.telepathdb.datamodels.Node(node.getId());
  }

  public static List<Node> toNodeList(List<com.telepathdb.datamodels.Node> nodes) {
    return nodes.stream()
        .map(PathDBWrapper::toNode)
        .collect(Collectors.toList());
  }

  public static List<com.telepathdb.datamodels.Node> fromNodeList(List<Node> nodes) {
    return nodes.stream()
        .map(PathDBWrapper::fromNode)
        .collect(Collectors.toList());
  }
}
