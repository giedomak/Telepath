package com.github.giedomak.telepathdb.datamodels.graph

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.utilities.Logger
import java.util.stream.Stream

/**
 * Data class for holding streams of paths where we abstract away the materializing into the MemoryManager.
 *
 * @property paths Stream of paths.
 * @property paths Supplier of streams of paths.
 * @param telepathDB If this module is given, we materialize the stream.
 */
class PathStream(
        telepathDB: TelepathDB?,
        private val _paths: Stream<Path>,
        val materialize: Boolean = true
) {

    // We only care about the memory manager
    private val memoryManager = telepathDB?.memoryManager

    // Backing field for saving in which spot the memory manager granted us space.
    private var memoryManagerId: Long? = null

    // Supply paths on demand either from the materialized memory manager, or our own _paths.
    val paths get() = if (materialize) memoryManager!![memoryManagerId!!] else _paths

    init {

        if (materialize) {
            // Materialize the paths into the memory manager
            memoryManagerId = memoryManager!!.add(_paths)
            Logger.debug("Paths materialized with id: $memoryManagerId")
        }

    }

}