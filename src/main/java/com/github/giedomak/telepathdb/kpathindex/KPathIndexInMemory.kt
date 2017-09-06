/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.kpathindex

import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.github.giedomak.telepathdb.datamodels.integrations.PathDBWrapper
import com.github.giedomak.telepathdb.memorymanager.spliterator.FixedBatchSpliterator
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory
import com.pathdb.statistics.StatisticsStoreReader
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * InMemory implementation of the KPathIndex.
 */
class KPathIndexInMemory(override var insertCallback: ((Path) -> Unit)? = null) : KPathIndex {

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
        return StreamSupport.stream(
                FixedBatchSpliterator(
                        pathIndex.getPaths(
                                PathDBWrapper.toPathPrefix(pathPrefix)
                        ).spliterator()
                ), false
        ).map { PathDBWrapper.fromPath(it) }
    }

    /**
     * Insert method to insert a Path into the KPathIndex.
     *
     * @param path The path we will insert into the KPathIndex.
     */
    override fun insert(path: Path) {
        // Insertion into PathDB
        pathIndex.insert(PathDBWrapper.toPath(path))

        // Invoke the callback
        insertCallback?.invoke(path)
    }

    /**
     * Delegate the statisticsStore to PathDB.
     */
    fun getStatisticsStore(): StatisticsStoreReader {
        return pathIndex.statisticsStore
    }
}
