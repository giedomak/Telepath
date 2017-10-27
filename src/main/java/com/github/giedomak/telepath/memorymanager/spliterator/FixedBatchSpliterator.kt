/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.memorymanager.spliterator

import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport.stream

class FixedBatchSpliterator<T> @JvmOverloads constructor(
        private val spliterator: Spliterator<T>,
        batchSize: Int = 64,
        est: Long = spliterator.estimateSize()
) : FixedBatchSpliteratorBase<T>(spliterator.characteristics(), batchSize, est) {

    override fun tryAdvance(action: Consumer<in T>): Boolean {
        return spliterator.tryAdvance(action)
    }

    override fun forEachRemaining(action: Consumer<in T>) {
        spliterator.forEachRemaining(action)
    }

    companion object {

        fun <T> withBatchSize(input: Stream<T>, batchSize: Int): Stream<T> {
            return stream(FixedBatchSpliterator(input.spliterator(), batchSize), true)
        }

        fun <T> batchedSpliterator(toWrap: Spliterator<T>, batchSize: Int): FixedBatchSpliterator<T> {
            return FixedBatchSpliterator(toWrap, batchSize)
        }
    }
}