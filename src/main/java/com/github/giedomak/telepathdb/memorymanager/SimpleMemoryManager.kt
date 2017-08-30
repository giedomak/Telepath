/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.memorymanager

import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.memorymanager.spliterator.PartitioningSpliterator.Companion.partition
import com.github.giedomak.telepathdb.utilities.Logger
import org.apache.commons.lang3.SerializationUtils
import org.parboiled.common.Tuple2
import java.io.*
import java.util.stream.Stream
import kotlin.streams.toList

/**
 * Let all results flow through this memory manager so we have control over the data and caching.
 */
object SimpleMemoryManager : MemoryManager {

    private const val MEMORY_BUDGET: Int = 10_000_000
    private const val CACHE_BUDGET: Int = 100_000
    private const val PARTITION_SIZE: Int = 1_000
    private const val BATCH_SIZE: Int = 100

    // HashMaps in which we store the data
    private val pathHashMap = hashMapOf<Long, MutableList<List<Path>>>()
    private val fileHashMap = hashMapOf<Long, MutableList<File>>()

    // Small cache holding one piece of data
    private var cache = Tuple2(-1L, emptyList<Path>())

    // Keep track of how much memory we have used so far
    private var memoryUsed = 0

    private var maxId = 0L
        set(value) {
            // Set the MaxId to the current id if greater
            if (value > maxId) field = value
        }

    //
    // ------- OPERATOR FUNCTIONS -------
    //

    override operator fun set(id: Long, path: Path): Long {
        collectPartition(id, listOf(path))
        return id
    }

    override operator fun set(id: Long, paths: Stream<Path>): Long {

        // Partition the existingStream into a stream with Lists of Paths
        val partitioned = partition(paths, PARTITION_SIZE, BATCH_SIZE)

        // Collect each partition into files or into memory
        partitioned.forEach { collectPartition(id, it) }

        return id
    }

    override operator fun get(id: Long): Stream<Path> {
        // Returns the combined results from our in-memory collection, and from what is stored on disk
        return getCombinedResults(id)
    }

    //
    // ------- FUNCTIONS -------
    //

    override fun add(paths: Stream<Path>): Long {
        return set(++maxId, paths)
    }

    /**
     * Clear all stored data from the SimpleMemoryManager, temp files will be deleted on JVM exit.
     */
    override fun clear() {
        pathHashMap.clear()
        fileHashMap.clear()
        cache = Tuple2(-1L, emptyList<Path>())
        memoryUsed = 0
        maxId = 0L
    }

    /**
     * Boolean value indicating of our new partition will fit into memory.
     *
     * This has to be public in order for our specs to mock it...
     */
    fun fitsIntoMemory(partition: List<Path>): Boolean {
        return memoryUsed + partition.size <= MEMORY_BUDGET
    }

    //
    // ------- PRIVATE FUNCTIONS -------
    //

    private fun storeInCacheWhenPossible(id: Long, paths: List<List<Path>>, files: List<File>): Boolean {

        if (paths.size * PARTITION_SIZE + files.size * PARTITION_SIZE < CACHE_BUDGET) {
            // Small enough for our cache, so store and return true
            cache = Tuple2(id, getCombinedResults(paths, files).toList())
            return true
        }

        return false
    }

    private fun getCombinedResults(paths: List<List<Path>>, files: List<File>): Stream<Path> {

        // We don't want to assign to incoming params in method bodies. So copy to local variable.
        var _paths = paths

        if (_paths.isEmpty()) {
            _paths = emptyList<List<Path>>()
            // throw new IllegalArgumentException("whoops");
        }

        // Gather the in-memory partitions and the partitions which are written to disk
        return Stream.concat(
                _paths.stream()
                        .flatMap { it.stream() },
                files.stream()
                        .map { readPartition(it) }
                        .flatMap { it.stream() }
        )
    }

    private fun getCombinedResults(id: Long): Stream<Path> {

        if (cache.a == id) {
            return cache.b.parallelStream()
        }

        val paths = pathHashMap.getOrDefault(id, emptyList<List<Path>>())
        val files = fileHashMap.getOrDefault(id, emptyList<File>())

        // Store and use the cache when possible
        if (storeInCacheWhenPossible(id, paths, files))
            return cache.b.parallelStream()

        // Otherwise we just stream our results without the cache
        return getCombinedResults(paths, files)
    }

    private fun collectPartition(id: Long, partition: List<Path>) {

        if (partition.isEmpty())
            return

        // Check if we have enough memory left, or if we need to write to disk
        if (fitsIntoMemory(partition)) {
            // Save the lists
            val paths = pathHashMap.getOrPut(id, { mutableListOf() })
            paths.add(partition)

            memoryUsed += partition.size

        } else {
            // Save the files
            writePartition(id, partition)
        }

        maxId = id
    }

    /**
     * Write a partition of List<Path> to disk.
     *
     * @param id        The intermediate result identifier (might consist out of multiple partitions).
     * @param partition The List<Path> partition we need to store on disk.
     */
    private fun writePartition(id: Long, partition: List<Path>) {

        if (partition.isEmpty())
            return

        try {
            // Create the temp file
            val temp = File.createTempFile("partition_" + id + "_", ".tmp")
            temp.deleteOnExit() // delete on JVM exit

            // Add this file to our intermediateResults
            val fileHandles = fileHashMap.getOrPut(id, { mutableListOf() })
            fileHandles.add(temp)

            // Try-with-resources will auto close our streams
            ObjectOutputStream(FileOutputStream(temp)).use { oos ->
                // Write our partition into the file as a byte[]
                oos.writeObject(serialize(partition))
                oos.flush()

                // Done
                Logger.debug("Partition written to: " + temp.absolutePath)
            }

        } catch (e: Exception) {
            Logger.error("Problem serializing: " + e)
        }
    }

    /**
     * Read and deserialize a partition file back into a List<Path> Object.
     *
     * @param file The file we need to deserialize.
     * @return The deserialized file into a [List] of [Path].
     */
    private fun readPartition(file: File): List<Path> {

        // Logger.debug("readPartition() file: " +file.getAbsolutePath());

        // Try-with-resources will auto close our streams
        try {
            @Suppress("UNCHECKED_CAST")
            ObjectInputStream(FileInputStream(file)).use { ois -> return deserialize(ois.readObject() as ByteArray) as List<Path> }
        } catch (e: Exception) {
            Logger.error("Problem deserializing: " + e)
            Logger.error("File: " + file.absolutePath)
            throw e
        }
    }

    /**
     * Serialize any Object into a byte array.
     *
     * @param partition The partition we need to serialize into a byte array.
     * @return The serialized partition into a byte array.
     */
    private fun serialize(partition: Any): ByteArray {
        return SerializationUtils.serialize(partition as Serializable)
    }

    /**
     * Deserialize a byte array back into a Object.
     *
     * @param data The byte array.
     * @return The object.
     */
    private fun deserialize(data: ByteArray): Any {
        return SerializationUtils.deserialize<Any>(data)
    }

}
