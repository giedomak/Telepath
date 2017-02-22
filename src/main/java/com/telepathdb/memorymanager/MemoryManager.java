/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.memorymanager;

import com.telepathdb.datamodels.Path;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.telepathdb.memorymanager.spliterator.PartitioningSpliterator.partition;

/**
 * Created by giedomak on 17/02/2017.
 */
public final class MemoryManager {

  private static final int PARTITION_SIZE = 10000;
  private static final int BATCH_SIZE = 10;

  private static HashMap<Long, Boolean> tooBigHashMap = new HashMap<>();
  private static HashMap<Long, Stream<Path>> streamHashMap = new HashMap<>();
  private static HashMap<Long, List<File>> fileHashMap = new HashMap<>();
  private static long maxId = 0;

  public static long put(Stream<Path> stream) {
    return put(++maxId, stream);
  }

  public static long put(long id, Stream<Path> stream) {

    System.out.println(stream.spliterator().getExactSizeIfKnown());
    boolean tooBig = true;
    tooBigHashMap.put(id, tooBig);

    if(tooBig) {
      // Save the files
      // Partition the existingStream into a stream with Lists of Paths
      Stream<List<Path>> partitioned = partition(stream, PARTITION_SIZE, BATCH_SIZE);

      partitioned
          .forEach(partition -> MemoryManager.writePartition(id, partition));

    } else {
      // Save the stream
      streamHashMap.put(id, stream);
    }

    if (id > maxId)
      maxId = id;

    return id;
  }

  public static Stream<Path> get(Long id) {

    if (tooBigHashMap.get(id)) {
      return fileHashMap.get(id).stream()
          .map(MemoryManager::readPartition)
          .flatMap(list -> list.stream());
    } else {
      return streamHashMap.get(id);
    }
  }

  public static Supplier<Stream<Path>> streamSupplier(Stream<Path> stream) {
    long id = put(stream);
    return () -> get(id);
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
