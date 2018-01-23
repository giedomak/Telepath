package com.github.giedomak.telepath.datamodels.graph

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.utilities.Logger
import java.util.function.Supplier
import java.util.stream.Stream

/**
 * Data class for holding streams of paths where we abstract away the materializing into the MemoryManager.
 *
 * @property paths Stream of paths. This acts as a Supplier if the paths are materialized.
 * @property materialize Boolean indicating if the given stream has to be materialized.
 * @param telepath A path stream must know about the Telepath module in order to materialize.
 */
class PathStream(
        telepath: Telepath?,
        private val _paths: Stream<Path>,
        val materialize: Boolean = true
) : Iterable<Path> {

    override fun iterator(): Iterator<Path> {
        return paths.iterator()
    }

    // We only care about the memory manager
    private val memoryManager = telepath?.memoryManager

    // Backing field for saving in which spot the memory manager granted us space.
    private var memoryManagerId: Long? = null

    // Return paths either from the materialized memory manager, or our own _paths.
    val paths get() = if (memoryManagerId != null) memoryManager!![memoryManagerId!!] else _paths

    // Supplier of our paths; make sure the paths are materialized!
    val pathSupplier get() = Supplier { paths }

    init {
        if (materialize) ensureMaterialization()
    }

    /**
     * Materialize the stream if we don't have a memoryManagerId yet.
     *
     * @return Returns this for chaining purposes.
     */
    fun ensureMaterialization(): PathStream {
        if (memoryManagerId == null) {
            // Materialize the paths into the memory manager
            memoryManagerId = memoryManager!!.add(_paths)
            Logger.debug("Paths materialized with id: $memoryManagerId")
        }

        // For chaining purposes
        return this
    }

}
