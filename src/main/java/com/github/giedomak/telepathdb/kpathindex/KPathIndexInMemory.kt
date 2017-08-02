/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.kpathindex

import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import com.github.giedomak.telepathdb.datamodels.integrations.PathDBWrapper
import com.github.giedomak.telepathdb.memorymanager.spliterator.FixedBatchSpliterator
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory
import java.io.IOException
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * InMemory implementation of the KPathIndex.
 */
class KPathIndexInMemory : KPathIndex {

    // Populates our pathIndex property with the InMemoryIndex obtained from the InMemoryIndexFactory from the com.pathdb package.
    private val pathIndex: com.pathdb.pathIndex.PathIndex = InMemoryIndexFactory().inMemoryIndex
    override var k = 0

    /**
     * Search method to lookup paths in the KPathIndex.
     *
     * @param pathPrefix The prefix of a path which we need to search.
     * @return A Stream with Paths which satisfy the pathPrefix.
     */
    override fun search(pathPrefix: PathPrefix): Stream<Path> {
        // We have to cast the Path model from pathDB's one, to our own again
        try {
            return StreamSupport.stream(
                    FixedBatchSpliterator(
                            pathIndex.getPaths(
                                    PathDBWrapper.toPathPrefix(pathPrefix)
                            ).spliterator()
                    ), true
            ).map { PathDBWrapper.fromPath(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Stream.empty<Path>()
    }

    /**
     * Insert method to insert a Path into the KPathIndex.
     *
     * @param path The path we will insert into the KPathIndex.
     */
    override fun insert(path: Path) {
        pathIndex.insert(PathDBWrapper.toPath(path))
    }
}
