package com.github.giedomak.telepath.kpathindex

import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathPrefix
import com.github.giedomak.telepath.datamodels.integrations.PathDBWrapper
import com.github.giedomak.telepath.memorymanager.spliterator.FixedBatchSpliterator
import com.google.common.io.Files
import com.jakewharton.byteunits.BinaryByteUnit
import com.pathdb.pathIndex.persisted.LMDBIndexFactory
import com.pathdb.statistics.StatisticsStoreReader
import java.io.File
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class KPathIndexDisk(
        override var insertCallback: ((Path) -> Unit)? = null,
        dir: File = Files.createTempDir()
) : KPathIndex {

    private val pathIndex: com.pathdb.pathIndex.PathIndex =
            LMDBIndexFactory(dir)
                    .withMaxDBSize(45, BinaryByteUnit.GIBIBYTES)
                    .build()

    override var k = 0

    /**
     * Search method to lookup paths in the KPathIndex.
     *
     * @param pathPrefix The prefix of a path which we need to search.
     * @return A Stream with Paths which satisfy the pathPrefix.
     */
    override fun search(pathPrefix: PathPrefix): Stream<Path> {
        // We have to cast the Path model from pathDB's one, to our own again
        return StreamSupport.stream(
                FixedBatchSpliterator(
                        Spliterators.spliteratorUnknownSize(
                                pathIndex.getPaths(
                                        PathDBWrapper.toPathPrefix(pathPrefix)
                                ).iterator()
                        , Spliterator.SORTED or Spliterator.ORDERED or Spliterator.NONNULL or Spliterator.DISTINCT or Spliterator.IMMUTABLE)
                ), false
        ).map { PathDBWrapper.fromPath(it) }
    }

    /**
     * Insert method to insert a Path into the KPathIndex.
     *
     * @param path The path we will insert into the KPathIndex.
     */
    override fun insert(path: Path, dryRun: Boolean) {
        // Insertion into PathDB
        if (!dryRun) pathIndex.insert(PathDBWrapper.toPath(path))

        // Invoke the callback
        insertCallback?.invoke(path)
    }

    /**
     * Delegate the statisticsStore to PathDB.
     */
    fun getStatisticsStore(): StatisticsStoreReader {
        return pathIndex.statisticsStore
    }
}
