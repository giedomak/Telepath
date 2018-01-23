/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.memorymanager.spliterator

import java.util.*
import java.util.Spliterators.spliterator
import java.util.function.Consumer

abstract class FixedBatchSpliteratorBase<T> @JvmOverloads constructor(
        characteristics: Int = Spliterator.IMMUTABLE or Spliterator.ORDERED or Spliterator.NONNULL,
        private val batchSize: Int = 128,
        private var est: Long = java.lang.Long.MAX_VALUE
) : Spliterator<T> {

    private val characteristics = characteristics or Spliterator.SUBSIZED

    override fun trySplit(): Spliterator<T>? {
        val holder = HoldingConsumer<T>()
        if (!tryAdvance(holder)) return null
        val a = arrayOfNulls<Any>(batchSize)
        var j = 0
        do a[j] = holder.value while (++j < batchSize && tryAdvance(holder))
        if (est != java.lang.Long.MAX_VALUE) est -= j.toLong()
        return spliterator(a, 0, j, characteristics() or Spliterator.SIZED)
    }

    override fun getComparator(): Comparator<in T>? {
        if (hasCharacteristics(Spliterator.SORTED)) return null
        throw IllegalStateException()
    }

    override fun estimateSize(): Long {
        return est
    }

    override fun characteristics(): Int {
        return characteristics
    }

    internal class HoldingConsumer<T> : Consumer<T> {

        var value: T? = null

        override fun accept(value: T) {
            this.value = value
        }
    }
}
