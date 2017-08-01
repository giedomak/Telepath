/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.integrations

import java.util.stream.Collectors

/**
 * Convert our data classes from and to the ones from [com.pathdb].
 */
object PathDBWrapper {

    fun toPath(path: com.github.giedomak.telepathdb.datamodels.Path): com.pathdb.pathIndex.Path {
        return com.pathdb.pathIndex.Path(path.pathId, toNodeList(path.nodes))
    }

    fun fromPath(path: com.pathdb.pathIndex.Path): com.github.giedomak.telepathdb.datamodels.Path {
        return com.github.giedomak.telepathdb.datamodels.Path(path.pathId, fromNodeList(path.nodes))
    }

    fun toPathPrefix(pathPrefix: com.github.giedomak.telepathdb.datamodels.PathPrefix): com.pathdb.pathIndex.PathPrefix {
        return com.pathdb.pathIndex.PathPrefix(pathPrefix.pathId, pathPrefix.length, toNodeList(pathPrefix.nodes))
    }

    fun toNode(node: com.github.giedomak.telepathdb.datamodels.Node): com.pathdb.pathIndex.Node {
        return com.pathdb.pathIndex.Node(node.id)
    }

    fun fromNode(node: com.pathdb.pathIndex.Node): com.github.giedomak.telepathdb.datamodels.Node {
        return com.github.giedomak.telepathdb.datamodels.Node(node.id)
    }

    fun toNodeList(nodes: List<com.github.giedomak.telepathdb.datamodels.Node>): List<com.pathdb.pathIndex.Node> {
        return nodes.stream()
                .map { toNode(it) }
                .collect(Collectors.toList())
    }

    fun fromNodeList(nodes: List<com.pathdb.pathIndex.Node>): List<com.github.giedomak.telepathdb.datamodels.Node> {
        return nodes.stream()
                .map { fromNode(it) }
                .collect(Collectors.toList())
    }
}
