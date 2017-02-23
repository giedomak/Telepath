/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.memorymanager;

import com.telepathdb.datamodels.Path;
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

  private static final int MEMORY_BUDGET = 3_000;
  private static final int CACHE_BUDGET = 10_000;
  private static final int PARTITION_SIZE = 1_000;
  private static final int BATCH_SIZE = 10;

  private static HashMap<Long, Boolean> tooBigHashMap = new HashMap<>();
  private static HashMap<Long, List<List<Path>>> pathHashMap = new HashMap<>();
  private static HashMap<Long, List<File>> fileHashMap = new HashMap<>();

  private static Tuple2<Long, List<Path>> cache = new Tuple2<Long, List<Path>>(-1l, Collections.emptyList());

  private static int memoryUsed = 0;
  private static long maxId = 0;

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
    // returned the combined results from our in-memory collection, and from what is stored on disk
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
        paths.parallelStream()
            .flatMap(list -> list.parallelStream()),
        files.parallelStream()
            .map(MemoryManager::readPartition)
            .flatMap(list -> list.parallelStream()));

  }

  private static Stream<Path> getCombinedResults(long id) {

    if(cache.a == id) {
      return cache.b.parallelStream();
    }

    List<List<Path>> paths = pathHashMap.getOrDefault(id, Collections.emptyList());
    List<File> files = fileHashMap.getOrDefault(id, Collections.emptyList());

    // Store and use the cache when possible
    if(storeInCacheWhenPossible(id, paths, files))
      return cache.b.parallelStream();

    // Otherwise we just stream our results without the cache
    return getCombinedResults(paths, files);
  }

  private static void collectPartition(long id, List<Path> partition) {

    if (partition.isEmpty())
      return;

    boolean tooBig = memoryUsed + partition.size() > MEMORY_BUDGET;
    tooBigHashMap.put(id, tooBig);

    if (tooBig) {
      // Save the files
      MemoryManager.writePartition(id, partition);

    } else {
      // Save the lists
      List<List<Path>> paths = pathHashMap.getOrDefault(id, new ArrayList<List<Path>>());
      paths.add(partition);
      pathHashMap.put(id, paths);

      setMemoryUsed(partition.size());
    }

    setMaxId(id);
  }

  private static void setMemoryUsed(int size) {
    memoryUsed += size;
  }

  private static void setMaxId(long id) {
    // Set the MaxId to the current id when needed
    if (id > maxId)
      maxId = id;
  }

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

      // Write our partition into the file as a byte[]
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp));
      oos.writeObject(serialize(partition));
      oos.flush();

      // Done
      System.out.println("Partition written to: " + temp.getAbsolutePath());

    } catch (Exception e) {
      System.out.println("Problem serializing: " + e);
    }
  }

  private static List<Path> readPartition(File file) {

    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
      return (List<Path>) deserialize((byte[]) ois.readObject());
    } catch (Exception e) {
      System.out.println("Problem deserializing: " + e);
      System.out.println("File: " + file.getAbsolutePath());
    }
    return null;
  }

  private static byte[] serialize(Object paths) {
    return SerializationUtils.serialize((Serializable) paths);
  }

  private static Object deserialize(byte[] data) {
    return SerializationUtils.deserialize(data);
  }

}
