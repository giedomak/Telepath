package com.github.giedomak.telepathdb.datamodels.graph

import com.github.giedomak.telepathdb.TelepathDB
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
        telepathDB: TelepathDB? = null
) {

    val pathSupplier get() = Supplier { paths }

    init {

        if (telepathDB != null) {
            paths = telepathDB.memoryManager[telepathDB.memoryManager.add(paths)]
        }

    }

}