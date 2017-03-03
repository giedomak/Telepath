/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.memorymanager;

import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.utilities.Logger;
import com.telepathdb.physicallibrary.PhysicalLibrary;

import org.apache.commons.lang3.SerializationUtils;
import org.parboiled.common.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telepathdb.memorymanager.spliterator.PartitioningSpliterator.partition;

/**
 * Created by giedomak
 */
public final class MemoryManager {

  private static final int MEMORY_BUDGET = 10_000_000;
  private static final int CACHE_BUDGET = 100_000;
  private static final int PARTITION_SIZE = 10_000;
  private static final int BATCH_SIZE = 100;


  private static HashMap<Long, List<List<Path>>> pathHashMap;
  private static HashMap<Long, List<File>> fileHashMap;

  private static Tuple2<Long, List<Path>> cache;

  private static int memoryUsed;
  private static long maxId;

  // "Static constructor"
  static {
    clear();
  }

  /**
   * Clear all stored data from the MemoryManager.
   * Temp files will be deleted on JVM exit.
   */
  public static void clear() {
    pathHashMap = new HashMap<>();
    fileHashMap = new HashMap<>();
    cache = new Tuple2<Long, List<Path>>(-1l, Collections.emptyList());
    memoryUsed = 0;
    maxId = 0;
  }

  public static long put(Stream<Path> stream) {
    return put(++maxId, stream);
  }

  public static long put(long id, Stream<Path> stream) {

    // Partition the existingStream into a stream with Lists of Paths
    Stream<List<Path>> partitioned = partition(stream, PARTITION_SIZE, BATCH_SIZE);

    // Collect each partition into files or into memory
    partitioned.forEach(partition -> collectPartition(id, partition));

    return id;
  }

  public static Stream<Path> get(Long id) {
    // Returns the combined results from our in-memory collection, and from what is stored on disk
    return getCombinedResults(id);
  }

  public static Supplier<Stream<Path>> streamSupplier(Stream<Path> stream) {
    long id = put(stream);
    return () -> get(id);
  }

  private static boolean storeInCacheWhenPossible(long id, List<List<Path>> paths, List<File> files) {

    if (paths.size() * PARTITION_SIZE + files.size() * PARTITION_SIZE < CACHE_BUDGET) {
      // Small enough for our cache, so store and return true
      cache = new Tuple2<>(id, getCombinedResults(paths, files).collect(Collectors.toList()));
      return true;
    }

    return false;
  }

  private static Stream<Path> getCombinedResults(List<List<Path>> paths, List<File> files) {
    // Gather the in-memory partitions and the partitions which are written to disk
    return PhysicalLibrary.union(
        paths.stream()
            .flatMap(list -> list.stream()),
        files.stream()
            .map(MemoryManager::readPartition)
            .flatMap(list -> list.stream()));

  }

  private static Stream<Path> getCombinedResults(long id) {

    if (cache.a == id) {
      return cache.b.parallelStream();
    }

    List<List<Path>> paths = pathHashMap.getOrDefault(id, Collections.emptyList());
    List<File> files = fileHashMap.getOrDefault(id, Collections.emptyList());

    // Store and use the cache when possible
    if (storeInCacheWhenPossible(id, paths, files))
      return cache.b.parallelStream();

    // Otherwise we just stream our results without the cache
    return getCombinedResults(paths, files);
  }

  private static void collectPartition(long id, List<Path> partition) {

    if (partition.isEmpty())
      return;

    // Check if we have enough memory left, or if we need to write to disk
    if (memoryUsed + partition.size() > MEMORY_BUDGET) {
      // Save the files
      MemoryManager.writePartition(id, partition);

    } else {
      // Save the lists
      List<List<Path>> paths = pathHashMap.getOrDefault(id, new ArrayList<List<Path>>());
      paths.add(partition);
      pathHashMap.put(id, paths);

      increaseMemoryUsed(partition.size());
    }

    setMaxId(id);
  }

  private static void increaseMemoryUsed(int size) {
    memoryUsed += size;
  }

  private static void setMaxId(long id) {
    // Set the MaxId to the current id if greater
    if (id > maxId)
      maxId = id;
  }

  /**
   * Write a partition of List<Path> to disk.
   *
   * @param id        The intermediate result identifier (might consist out of multiple partitions)
   * @param partition The List<Path> partition we need to store on disk
   */
  private static void writePartition(long id, List<Path> partition) {

    if (partition.isEmpty())
      return;

    try {
      // Create the temp file
      File temp = File.createTempFile("partition_" + id + "_", ".tmp");
      temp.deleteOnExit(); // delete on JVM exit

      // Add this file to our intermediateResults
      List<File> fileHandles = fileHashMap.getOrDefault(id, new ArrayList<File>());
      fileHandles.add(temp);
      fileHashMap.put(id, fileHandles);

      // Try-with-resources will auto close our streams
      try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp))) {
        // Write our partition into the file as a byte[]
        oos.writeObject(serialize(partition));
        oos.flush();

        // Done
        Logger.debug("Partition written to: " + temp.getAbsolutePath());
      }

    } catch (Exception e) {
      Logger.error("Problem serializing: " + e);
    }
  }

  /**
   * Read and deserialize a partition file back into a List<Path> Object.
   *
   * @param file The file we need to deserialize
   * @return The deserialized file into List<Path>
   */
  private static List<Path> readPartition(File file) {

    // Logger.debug("readPartition() file: " +file.getAbsolutePath());

    // Try-with-resources will auto close our streams
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (List<Path>) deserialize((byte[]) ois.readObject());
    } catch (Exception e) {
      Logger.error("Problem deserializing: " + e);
      Logger.error("File: " + file.getAbsolutePath());
    }
    return null;
  }

  /**
   * Serialize any Object into a byte array.
   *
   * @param partition The partition we need to serialize into a byte array.
   * @return The serialized partition into a byte array.
   */
  private static byte[] serialize(Object partition) {
    return SerializationUtils.serialize((Serializable) partition);
  }

  /**
   * Deserialize a byte array back into a Object.
   *
   * @param data The byte array.
   * @return The object.
   */
  private static Object deserialize(byte[] data) {
    return SerializationUtils.deserialize(data);
  }

}
