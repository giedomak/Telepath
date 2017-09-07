package com.github.giedomak.telepathdb.datamodels.graph

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.utilities.Logger
import java.util.function.Supplier
import java.util.stream.Stream

/**
 * Data class for holding streams of paths where we abstract away the materializing into the [MemoryManager].
 *
 * @property paths Stream of paths.
 * @property pathSupplier Supplier of streams of paths.
 * @param telepathDB If this module is given, we materialize the stream.
 */
class PathStream(
        var paths: Stream<Path>,
        private val telepathDB: TelepathDB? = TelepathDB
) {

    private val memoryManager get() = telepathDB!!.memoryManager
    private var memoryManagerId: Long? = null

    val pathSupplier get() = Supplier { memoryManager[memoryManagerId!!] }

    init {

        if (telepathDB != null) {
            memoryManagerId = memoryManager.add(paths)
            Logger.debug(memoryManagerId!!)
            paths = memoryManager[memoryManagerId!!]
        }

    }

}