package com.github.giedomak.telepath.physicaloperators.sortmergejoin

import java.util.*
import java.util.function.Consumer

/**
 * Recommend using with StreamSupport.stream(iteratorStream, false);
 */
class SpliteratorAdapter<T>(private val iterator: Iterator<T>, characteristics: Int = 0) : Spliterators.AbstractSpliterator<T>(java.lang.Long.MAX_VALUE, characteristics) {

    @Synchronized override fun tryAdvance(action: Consumer<in T>): Boolean {
        if (iterator.hasNext()) {
            action.accept(iterator.next())
            return true
        }
        return false
    }
}