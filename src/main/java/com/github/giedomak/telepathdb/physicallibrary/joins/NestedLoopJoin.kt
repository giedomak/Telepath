/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicallibrary.joins

import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import java.util.stream.Stream

/**
 * Nested-loop-join.
 */
object NestedLoopJoin {

    fun run(stream1: Stream<Path>, stream2: Stream<Path>): Stream<Path> {

        // Because we are doing a nested loop, we have to create the stream again for each new iteration.
        val streamSupplier = MemoryManager.streamSupplier(stream2)

        // Basically we are doing a nested loop to do an inner-join and concatenate the paths.
        return stream1.flatMap { v1 ->
            streamSupplier.get()
                    .filter { v2 -> v1.lastNode() == v2.firstNode() }
                    .map { v2 -> PathIdentifierStore.concatenatePaths(v1, v2) }
        }.parallel()
    }
}
