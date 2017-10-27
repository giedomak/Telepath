/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.memorymanager.spliterator

import java.util.*
import java.util.Spliterators.AbstractSpliterator
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport

class PartitioningSpliterator<E>(
        private val spliterator: Spliterator<E>,
        private val partitionSize: Int
) : AbstractSpliterator<List<E>>(spliterator.estimateSize(), spliterator.characteristics() or Spliterator.NONNULL) {

    init {
        if (partitionSize <= 0)
            throw IllegalArgumentException(
                    "Partition size must be positive, but was " + partitionSize)
    }

    override fun tryAdvance(action: Consumer<in List<E>>): Boolean {
        val partition = ArrayList<E>(partitionSize)
        while (spliterator.tryAdvance { partition.add(it) } && partition.size < partitionSize) {
        }
        if (partition.isEmpty()) return false
        action.accept(partition)
        return true
    }

    override fun estimateSize(): Long {
        val est = spliterator.estimateSize()
        return if (est == java.lang.Long.MAX_VALUE)
            est
        else
            est / partitionSize + if (est % partitionSize > 0) 1 else 0
    }

    companion object {

        fun <E> partition(input: Stream<E>, partitionSize: Int): Stream<List<E>> {
            return StreamSupport.stream(PartitioningSpliterator(input.spliterator(), partitionSize), false)
        }

        fun <E> partition(input: Stream<E>, partitionSize: Int, batchSize: Int): Stream<List<E>> {
            return StreamSupport.stream(
                    FixedBatchSpliterator(PartitioningSpliterator(input.spliterator(), partitionSize), batchSize), false)
        }
    }
}