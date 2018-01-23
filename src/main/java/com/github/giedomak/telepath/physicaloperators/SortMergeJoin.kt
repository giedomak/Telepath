/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.physicaloperators

import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import java.util.*
import java.util.function.Consumer
import java.util.stream.StreamSupport

//lass SortMergeJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {
//
//    override fun evaluate(): PathStream {
//
////        val leftSorted = IteratorBuffer(firstChild.evaluate().paths.iterator())
////        val rightSorted = IteratorBuffer(lastChild.evaluate().paths.iterator())
//
//        val partition1 = StreamSupport.stream(
//                PartitioningYaySpliterator(firstChild.evaluate().paths.spliterator(), { it.nodes.first().id }), false
//        )
//
//        val partition2 = StreamSupport.stream(
//                PartitioningYaySpliterator(lastChild.evaluate().paths.spliterator(), { it.nodes.first().id }), false
//        )
//
//        val results = mutableListOf<Path>()
//
//        val it1 = partition1.iterator().iterator()
//        val it2 = partition2.iterator().iterator()
//
//        var yay1 = it1.next()
//        var yay2 = it2.next()
//
//        while (it1.hasNext() && it2.hasNext()) {
//
//            println(yay1)
//            println(yay2)
//
//
//            if (yay1.first().nodes.first().id < yay2.first().nodes.first().id) {
//                yay1 = it1.next()
//            }
//            if (yay1.first().nodes.first().id > yay2.first().nodes.first().id) {
//                yay2 = it2.next()
//            }
//            if (yay1.first().nodes.first().id == yay2.first().nodes.first().id) {
//                results.addAll(
//                        yay1.flatMap { left ->
//                            println("Yay")
//                            yay2.map { right ->
//                                physicalPlan.query.telepath.pathIdentifierStore.concatenatePaths(left.inverse(), right)
//                            }
//                                    .filter { it != null }
//                                    .map { it!! }
//                        }
//                )
//                yay1 = it1.next()
//                yay2 = it2.next()
//            }
//
//
//        }

/**
 * Sort-merge-join.
 */
class SortMergeJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

    override fun evaluate(): PathStream {

//        val leftSorted = IteratorBuffer(firstChild.evaluate().paths.iterator())
//        val rightSorted = IteratorBuffer(lastChild.evaluate().paths.iterator())

        val partition1 = StreamSupport.stream(
                PartitioningYaySpliterator(firstChild.evaluate().paths.spliterator(), { it.nodes.first().id }), true
        )

        val partition2 = StreamSupport.stream(
                PartitioningYaySpliterator(lastChild.evaluate().paths.spliterator(), { it.nodes.first().id }), true
        )

        val results = mutableListOf<Path>()

        val it1 = partition1.iterator()
        val it2 = partition2.iterator()

        var yay1: List<Path>? = it1.next()
        var yay2: List<Path>? = it2.next()

        while (yay1 != null && yay2 != null) {

            if (yay1.first().nodes.first().id == yay2.first().nodes.first().id) {
                results.addAll(
                        yay1.flatMap { left ->
                            yay2!!.map { right ->
                                physicalPlan.query.telepath.pathIdentifierStore.concatenatePaths(left.inverse(), right)
                            }.filterNotNull()
                        }
                )
                yay1 = if (it1.hasNext()) it1.next() else null
                yay2 = if (it2.hasNext()) it2.next() else null

            }

            if (yay1 != null && yay2 != null && yay1.first().nodes.first().id > yay2.first().nodes.first().id) {
                yay2 = if (it2.hasNext()) it2.next() else null
            }

            if (yay1 != null && yay2 != null && yay1.first().nodes.first().id < yay2.first().nodes.first().id) {
                yay1 = if (it1.hasNext()) it1.next() else null
            }
        }

        return PathStream(
                physicalPlan.query.telepath,
                results.stream(),
                materialize = false
        )
    }


//    override fun evaluate(): PathStream {
//
//        val leftSorted = IteratorBuffer(firstChild.evaluate().paths.iterator())
//        val rightSorted = IteratorBuffer(lastChild.evaluate().paths.iterator())
//
//        if (!leftSorted.hasNext() || !rightSorted.hasNext()) return PathStream(physicalPlan.query.telepath, Stream.empty())
//
//        val results = mutableListOf<Path>()
//
//        var leftSubset = advance(leftSorted)
//        var rightSubset = advance(rightSorted)
//
//        while (leftSubset.isNotEmpty() && rightSubset.isNotEmpty()) {
//
//            if (key(leftSubset.last()) == key(rightSubset.last())) {
//                // add cartesian product of left_subset and right_subset to output
//                results.addAll(
//                        leftSubset.flatMap { left ->
//                            rightSubset.map { right ->
//                                physicalPlan.query.telepath.pathIdentifierStore.concatenatePaths(left.inverse(), right)
//                            }.filterNotNull()
//                        }
//                )
//                leftSubset = advance(leftSorted)
//                rightSubset = advance(rightSorted)
//
//            } else if (key(leftSubset.last()) < key(rightSubset.last())) {
//                leftSubset = advance(leftSorted)
//
//            } else {
//                rightSubset = advance(rightSorted)
//            }
//        }
//
//        return PathStream(
//                physicalPlan.query.telepath,
//                results.stream(),
//                materialize = false
//        )
//    }

    private fun key(path: Path): Long {
        return path.nodes.first().id
    }

    private fun advance(sorted: IteratorBuffer<Path>): MutableList<Path> {

        val subset = mutableListOf<Path>()

        val key = sorted.peek()?.nodes?.first()?.id

        while (sorted.hasNext() && sorted.peek()!!.nodes.first().id == key) {

            subset.add(sorted.next())

        }

        return subset
    }

    /**
     * The cost of our sort merge join implementation.
     */
    override fun cost(): Long {
        // The cost to produce results
        val myCost = firstChild.cardinality + lastChild.cardinality

        // Our input sets might be intermediate results, so take their cost into account.
        val cost1 = firstChild.cost()
        val cost2 = lastChild.cost()

        // Overflow check
        if (myCost == Long.MAX_VALUE || cost1 == Long.MAX_VALUE || cost2 == Long.MAX_VALUE) return Long.MAX_VALUE

        return myCost + cost1 + cost2
    }
}

/**
 * This is essentially a thin wrapper on top of a Iterator... which keeps
 * the last element in memory.
 */
internal class IteratorBuffer<out T> constructor(private val iterator: Iterator<T>) {

    private var cache: T? = null

    init {
        reload()
    }

    fun hasNext(): Boolean {
        return cache != null
    }

    fun peek(): T? {
        return cache
    }

    fun next(): T {
        val answer = peek()!!
        reload()
        return answer
    }

    private fun reload() {
        this.cache = if (iterator.hasNext()) iterator.next() else null
    }

}

//internal class PartitioningYaySpliterator<E>(
//        private val spliterator: Spliterator<E>,
//        private val block: (E) -> Serializable
//) : Spliterators.AbstractSpliterator<List<E>>(spliterator.estimateSize(), spliterator.characteristics() or Spliterator.NONNULL) {
//
//    private var last : E? = null
//
//    override fun tryAdvance(action: Consumer<in List<E>>): Boolean {
//        val partition = ArrayList<E>()
//        if (last != null) partition.add(last!!)
//        while (spliterator.tryAdvance { last = it } && (partition.isEmpty() || partition.last().run { block } == last.run { block })) {
//            partition.add(last!!)
//        }
//        if (partition.isEmpty()) return false
//        action.accept(partition)
//        return true
//    }
//
//    companion object {
//
//        fun <E> partition(input: Stream<E>, partitionSize: Int): Stream<List<E>> {
//            return StreamSupport.stream(PartitioningYaySpliterator(input.spliterator(), partitionSize), false)
//        }
//
////        fun <E> partition(input: Stream<E>, partitionSize: Int, batchSize: Int): Stream<List<E>> {
////            return StreamSupport.stream(
////                    FixedBatchSpliterator(PartitioningSpliterator(input.spliterator(), partitionSize), batchSize), false)
////        }
//    }
//}


//
//
///**
// * Copyright (C) 2016-2017 - All rights reserved.
// * This file is part of the telepath project which is released under the GPLv3 license.
// * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
// * You may use, distribute and modify this code under the terms of the GPLv3 license.
// */
//
//package com.github.giedomak.telepath.physicaloperators
//
//import com.github.giedomak.telepath.datamodels.graph.Path
//import com.github.giedomak.telepath.datamodels.graph.PathStream
//import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
//import java.io.Serializable
//import java.util.*
//import java.util.function.Consumer
//import java.util.stream.StreamSupport
//
///**
// * Sort-merge-join.
// */
//class SortMergeJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {
//
//    override fun evaluate(): PathStream {
//
////        val leftSorted = IteratorBuffer(firstChild.evaluate().paths.iterator())
////        val rightSorted = IteratorBuffer(lastChild.evaluate().paths.iterator())
//
//        val partition1 = StreamSupport.stream(
//                PartitioningYaySpliterator(firstChild.evaluate().paths.spliterator(), { it.nodes.first().id }), false
//        )
//
//        val partition2 = StreamSupport.stream(
//                PartitioningYaySpliterator(lastChild.evaluate().paths.spliterator(), { it.nodes.first().id }), false
//        )
//
//        val results = mutableListOf<Path>()
//
//        val it1 = partition1.iterator().iterator()
//        val it2 = partition2.iterator().iterator()
//
//        var yay1 = it1.next()
//        var yay2 = it2.next()
//
//        while (it1.hasNext() && it2.hasNext()) {
//
//            println(yay1)
//            println(yay2)
//
//
//            if (yay1.first().nodes.first().id < yay2.first().nodes.first().id) {
//                yay1 = it1.next()
//            }
//            if (yay1.first().nodes.first().id > yay2.first().nodes.first().id) {
//                yay2 = it2.next()
//            }
//            if (yay1.first().nodes.first().id == yay2.first().nodes.first().id) {
//                results.addAll(
//                        yay1.flatMap { left ->
//                            println("Yay")
//                            yay2.map { right ->
//                                physicalPlan.query.telepath.pathIdentifierStore.concatenatePaths(left.inverse(), right)
//                            }
//                                    .filter { it != null }
//                                    .map { it!! }
//                        }
//                )
//                yay1 = it1.next()
//                yay2 = it2.next()
//            }
//
//
//        }
//
////        if (!leftSorted.hasNext() || !rightSorted.hasNext()) return PathStream(physicalPlan.query.telepath, Stream.empty())
////
////        var leftSubset = advance(leftSorted)
////        var rightSubset = advance(rightSorted)
////
////        while (leftSorted.hasNext() && rightSorted.hasNext()) {
////
////            if (leftSubset.last().nodes.first().id == rightSubset.last().nodes.first().id) {
////                Logger.debug("add cartesian product of left_subset and right_subset to output")
////                // add cartesian product of left_subset and right_subset to output
////                results.addAll(
////                        leftSubset.flatMap { left ->
////                            println("Yay")
////                            rightSubset.map { right ->
////                                physicalPlan.query.telepath.pathIdentifierStore.concatenatePaths(left.inverse(), right)
////                            }
////                                    .filter { it != null }
////                                    .map { it!! }
////                        }
////                )
////                leftSubset = advance(leftSorted)
////                rightSubset = advance(rightSorted)
////
////            } else if (leftSubset.last().nodes.first().id < rightSubset.last().nodes.first().id) {
////                leftSubset = advance(leftSorted)
////
////            } else {
////                rightSubset = advance(rightSorted)
////            }
////        }
//
//        return PathStream(
//                physicalPlan.query.telepath,
//                results.stream(),
//                materialize = false
//        )
//    }
//
//    private fun advance(sorted: IteratorBuffer<Path>): Iterable<Path> {
//
//        val subset = mutableListOf<Path>()
//
//        val key = sorted.peek()?.nodes?.first()?.id
//
//        while (sorted.hasNext() && sorted.peek()!!.nodes.first().id == key) {
//
//            subset.add(sorted.next())
//
//        }
//
//        return subset
//    }
//
////    override fun evaluate(): PathStream {
////
////        val firstEvaluation = firstChild.evaluate()
////
////        val iterator = MergedIterator<Path>(firstEvaluation.paths.iterator(), lastChild.evaluate().paths.iterator())
////        val spliterator = SpliteratorAdapter(iterator, firstEvaluation.paths.spliterator().characteristics())
////        return PathStream(
////                physicalPlan.query.telepath,
////                StreamSupport.stream(spliterator, false),
////                materialize = false
////        )
////    }
//
//    /**
//     * The cost of our sort merge join implementation.
//     */
//    override fun cost(): Long {
//        // The cost to produce results
//        val myCost = firstChild.cardinality + lastChild.cardinality
//
//        // Our input sets might be intermediate results, so take their cost into account.
//        val cost1 = firstChild.cost()
//        val cost2 = lastChild.cost()
//
//        // Overflow check
//        if (myCost == Long.MAX_VALUE || cost1 == Long.MAX_VALUE || cost2 == Long.MAX_VALUE) return Long.MAX_VALUE
//
//        return myCost + cost1 + cost2
//    }
//}
//
///**
// * This is essentially a thin wrapper on top of a Iterator... which keeps
// * the last element in memory.
// */
//internal class IteratorBuffer<out T> constructor(private val iterator: Iterator<T>) {
//
//    private var cache: T? = null
//
//    init {
//        reload()
//    }
//
//    fun hasNext(): Boolean {
//        return cache != null
//    }
//
//    fun peek(): T? {
//        return cache
//    }
//
//    fun next(): T {
//        val answer = peek()!!
//        reload()
//        return answer
//    }
//
//    private fun reload() {
//        this.cache = if (iterator.hasNext()) iterator.next() else null
//    }
//
//}
internal class PartitioningYaySpliterator<Path>(
        private val spliterator: Spliterator<Path>,
        private val block: (Path) -> Long
) : Spliterators.AbstractSpliterator<List<Path>>(spliterator.estimateSize(), spliterator.characteristics() or Spliterator.NONNULL) {

    @Volatile
    private var last: Path? = null

    override fun tryAdvance(action: Consumer<in List<Path>>): Boolean {

        val partition = ArrayList<Path>()
        var key: Long = -1

        if (last != null) {
            partition.add(last!!)
            key = partition.last().let(block)
            last = null
        }

        while (spliterator.tryAdvance { partition.add(it) }) {
            if (partition.size > 1) {
                if (partition.last().let(block) != key) {
                    last = partition.last()
                    partition.removeAt(partition.size - 1)
                    break
                }
            }
            key = partition.last().let(block)
        }

        if (partition.isEmpty()) return false
        action.accept(partition)
        return true
    }

    companion object {

//        fun <E> partition(input: Stream<E>, partitionSize: Int): Stream<List<E>> {
//            return StreamSupport.stream(PartitioningYaySpliterator(input.spliterator(), partitionSize), false)
//        }

//        fun <E> partition(input: Stream<E>, partitionSize: Int, batchSize: Int): Stream<List<E>> {
//            return StreamSupport.stream(
//                    FixedBatchSpliterator(PartitioningSpliterator(input.spliterator(), partitionSize), batchSize), false)
//        }
    }
}
