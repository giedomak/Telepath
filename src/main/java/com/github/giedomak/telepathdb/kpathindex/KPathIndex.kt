/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.kpathindex

import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix

import java.io.IOException
import java.util.stream.Stream

/**
 * KPathIndex interface defining the public methods of the kpathindex package.
 */
interface KPathIndex {

    /**
     * Getter for K.
     *
     * K indicates the paths with up to this K number of edges are indexed in the kPathIndex.
     * For example: if K=2, the index contains all paths which have 1 or 2 edges along the path.
     */
    var k: Int

    /**
     * Search method to lookup paths in the KPathIndex.
     *
     * @param pathPrefix The prefix of a path which we need to search.
     * @return An Iterable with Paths which satisfy the pathPrefix.
     * @throws IOException I/O error.
     */
    fun search(pathPrefix: PathPrefix): Stream<Path>

    /**
     * Insert method to insert a Path into the KPathIndex.
     *
     * @param path The path we will insert into the KPathIndex.
     */
    fun insert(path: Path)
}
