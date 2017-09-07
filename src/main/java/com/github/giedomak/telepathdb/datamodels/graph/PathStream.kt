package com.github.giedomak.telepathdb.datamodels.graph

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.utilities.Logger
import java.util.function.Supplier
import java.util.stream.Stream

/**
 * Data class for holding streams of paths where we abstract away the materializing into the MemoryManager.
 *
 * @property paths Stream of paths. This acts as a Supplier if the paths are materialized.
 * @property materialize Boolean indicating if the given stream has to be materialized.
 * @param telepathDB A path stream must know about the TelepathDB module in order to materialization.
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

    // Return paths either from the materialized memory manager, or our own _paths.
    val paths get() = if (memoryManagerId != null) memoryManager!![memoryManagerId!!] else _paths

    // Supplier of our paths; make sure the paths are materialized!
    val pathSupplier get() = Supplier { paths }

    init {
        if (materialize) ensureMaterialization()
    }

    fun ensureMaterialization(): PathStream {
        if (memoryManagerId == null) {
            // Materialize the paths into the memory manager
            memoryManagerId = memoryManager!!.add(paths)
            Logger.debug("Paths materialized with id: $memoryManagerId")
        }

        // For chaining purposes
        return this
    }

}