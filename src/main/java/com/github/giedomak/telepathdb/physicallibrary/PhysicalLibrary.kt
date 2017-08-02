/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicallibrary

import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.physicallibrary.joins.HashJoin
import java.util.stream.Stream

/**
 * The physical operators we support.
 */
object PhysicalLibrary {

    private val DEFAULT_DISTINCT = true

    // ------------- UNION ---------------

    fun union(list1: List<Path>, list2: List<Path>): Stream<Path> {
        return union(list1.stream(), list2.stream(), DEFAULT_DISTINCT)
    }

    @JvmOverloads fun union(stream1: Stream<Path>, stream2: Stream<Path>, distinct: Boolean = DEFAULT_DISTINCT): Stream<Path> {
        // Out-of-the-box Java 8 Streams
        if (distinct) {
            return Stream.concat(stream1, stream2).distinct().parallel()
        } else {
            return Stream.concat(stream1, stream2).parallel()
        }
    }

    // ------------- CONCATENATION ---------------

    fun concatenation(stream1: Stream<Path>, stream2: Stream<Path>): Stream<Path> {
        return HashJoin.run(stream1, stream2)
    }
}
