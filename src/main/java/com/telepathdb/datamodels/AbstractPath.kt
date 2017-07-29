/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels

import java.io.Serializable

/**
 * Exists for abstracting the comparision logic between path-prefixes and paths.
 */
open class AbstractPath internal constructor(val pathId: Long) : Comparable<AbstractPath>, Serializable {

    override fun compareTo(other: AbstractPath): Int {
        if (this is PathPrefix) {
            if (other is Path) {
                return comparePathPrefixToPath(this, other)
            } else if (other is PathPrefix) {
                return comparePathPrefixToPathPrefix(this, other)
            }
        } else if (this is Path) {
            if (other is Path) {
                return comparePathToPath(this, other)
            } else if (other is PathPrefix) {
                // The comparision gets reversed here, so we need to invert the result.
                return -comparePathPrefixToPath(other, this)
            }
        }
        throw UnsupportedOperationException(
                String.format("Attempted comparision of unsupported types. Supported types are %s and %s.",
                        Path::class.java.name, PathPrefix::class.java.name))
    }

    //
    // ---------- PRIVATE FUNCTIONS ----------
    //

    private fun comparePathPrefixToPath(pathPrefix: PathPrefix, path: Path): Int {
        if (pathPrefix.pathId != path.pathId) {
            return if (pathPrefix.pathId > path.pathId) 1 else -1
        }
        if (pathPrefix.length != path.length) {
            return pathPrefix.length - path.length
        }
        for (i in 0..pathPrefix.prefixLength - 1) {
            if (pathPrefix.nodes[i].id - path.nodes[i].id != 0L) {
                return java.lang.Long.compare(pathPrefix.nodes[i].id, path.nodes[i].id)
            }
        }
        if (pathPrefix.prefixLength != path.length) {
            return pathPrefix.prefixLength - path.length
        }
        return 0
    }

    private fun comparePathPrefixToPathPrefix(pathPrefix1: PathPrefix, pathPrefix2: PathPrefix): Int {
        if (pathPrefix1 == pathPrefix2) {
            return 0
        }
        if (pathPrefix1.pathId != pathPrefix2.pathId) {
            return if (pathPrefix1.pathId > pathPrefix2.pathId) 1 else -1
        }
        if (pathPrefix1.length != pathPrefix2.length) {
            return pathPrefix1.length - pathPrefix2.length
        }
        if (pathPrefix1.prefixLength != pathPrefix2.prefixLength) {
            return pathPrefix1.length - pathPrefix2.length
        }
        for (i in 0..pathPrefix1.prefixLength - 1) {
            if (pathPrefix1.nodes[i].id - pathPrefix2.nodes[i].id != 0L) {
                return java.lang.Long.compare(pathPrefix1.nodes[i].id, pathPrefix2.nodes[i].id)
            }
        }
        return 0
    }

    private fun comparePathToPath(path1: Path, path2: Path): Int {
        if (path1 == path2) {
            return 0
        }
        if (path1.pathId != path2.pathId) {
            return if (path1.pathId > path2.pathId) 1 else -1
        }
        if (path1.length != path2.length) {
            return path1.length - path2.length
        }
        for (i in 0..path1.length - 1) {
            if (path1.nodes[i].id - path2.nodes[i].id != 0L) {
                return java.lang.Long.compare(path1.nodes[i].id, path2.nodes[i].id)
            }
        }
        return 0
    }
}
