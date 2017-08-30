package com.github.giedomak.telepathdb.memorymanager

import com.github.giedomak.telepathdb.datamodels.graph.Path
import java.util.stream.Stream

interface MemoryManager {

    operator fun get(id: Long): Stream<Path>

    fun add(paths: Stream<Path>): Long

    operator fun set(id: Long, path: Path): Long

    operator fun set(id: Long, paths: Stream<Path>): Long

    fun clear()

}