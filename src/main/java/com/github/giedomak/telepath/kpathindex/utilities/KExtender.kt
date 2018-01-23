/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.kpathindex.utilities

import com.github.giedomak.telepath.datamodels.graph.PathPrefix
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.physicaloperators.OpenHashJoin
import com.github.giedomak.telepath.utilities.Logger

/**
 * Extend the path index to a higher k-value.
 */
object KExtender {

    fun run(kPathIndex: KPathIndex, k: Int, dryRun: Boolean = false): Long {

        // Return if our kPathIndex is already of size k
        if (kPathIndex.k >= k) return 0

        Logger.debug("Current k: " + kPathIndex.k + ", target: " + k)

        // Current K of kPathIndex
        // FlatMaps the Stream of Streams<Path> into one 'flattened' Stream<Path>
        val source_k = PathIdentifierStore.getPathIds(kPathIndex.k)
                .stream()
                .flatMap { kPathIndex.search(PathPrefix(it)) }

        // K = 1
        val k1 = PathIdentifierStore.getPathIds(1)
                .stream()
                .flatMap { kPathIndex.search(PathPrefix(it)) }

        // Concatenate the current K paths, with the K=1 paths so we get the K=K+1 paths
        val paths = OpenHashJoin(
                stream1 = PathStream(null, source_k, false),
                stream2 = PathStream(null, k1, false),
                materialize = true,
                parallel = false
        ).evaluate().paths

        var count = 0

        var start = System.currentTimeMillis()

        // Make sure we insert after we collected the results, otherwise we get a concurrency exception
        // because we are inserting while we haven't consumed the whole stream yet.
        paths.forEach {
            kPathIndex.insert(it, dryRun)
            count++
            if (count % 5000 == 0) {
                val secs = (System.currentTimeMillis() - start) / 1_000f
                val speed = (5000 / secs).toInt()
                Logger.debug("\r Currently inserted: $count, Speed: $speed per second.", false)
                start = System.currentTimeMillis()

            }
        }

        Logger.debug("\nConcatenation done: $count")

        // Set K to K+1
        kPathIndex.k = kPathIndex.k + 1

        // Recursive call until we reach our target k
        return count + run(kPathIndex, k)
    }
}
