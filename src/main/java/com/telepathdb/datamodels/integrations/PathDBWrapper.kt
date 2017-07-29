/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.integrations

import java.util.stream.Collectors

/**
 * Convert our data classes from and to the ones from [com.pathdb].
 */
object PathDBWrapper {

    fun toPath(path: com.telepathdb.datamodels.Path): com.pathdb.pathIndex.Path {
        return com.pathdb.pathIndex.Path(path.pathId, toNodeList(path.nodes))
    }

    fun fromPath(path: com.pathdb.pathIndex.Path): com.telepathdb.datamodels.Path {
        return com.telepathdb.datamodels.Path(path.pathId, fromNodeList(path.nodes))
    }

    fun toPathPrefix(pathPrefix: com.telepathdb.datamodels.PathPrefix): com.pathdb.pathIndex.PathPrefix {
        return com.pathdb.pathIndex.PathPrefix(pathPrefix.pathId, pathPrefix.length, toNodeList(pathPrefix.nodes))
    }

    fun toNode(node: com.telepathdb.datamodels.Node): com.pathdb.pathIndex.Node {
        return com.pathdb.pathIndex.Node(node.id)
    }

    fun fromNode(node: com.pathdb.pathIndex.Node): com.telepathdb.datamodels.Node {
        return com.telepathdb.datamodels.Node(node.id)
    }

    fun toNodeList(nodes: List<com.telepathdb.datamodels.Node>): List<com.pathdb.pathIndex.Node> {
        return nodes.stream()
                .map { toNode(it) }
                .collect(Collectors.toList())
    }

    fun fromNodeList(nodes: List<com.pathdb.pathIndex.Node>): List<com.telepathdb.datamodels.Node> {
        return nodes.stream()
                .map { fromNode(it) }
                .collect(Collectors.toList())
    }
}
