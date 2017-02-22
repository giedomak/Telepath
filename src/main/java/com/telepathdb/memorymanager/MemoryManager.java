/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.memorymanager;

import com.telepathdb.datamodels.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telepathdb.memorymanager.spliterator.PartitioningSpliterator.partition;

/**
 * Created by giedomak on 17/02/2017.
 */
public final class MemoryManager {

  private static HashMap<Long, List<File>> intermediateResults = new HashMap<>(); // In-memory for now
  private static long maxId = 0;

  public static void put(Long id, Stream<Path> stream) {
    // Partition the existingStream into a stream with Lists of Paths
    Stream<List<Path>> partitioned = partition(stream, 100, 1);

    partitioned
        .forEach(partition -> MemoryManager.writePartition(id, partition));
  }

  public static List<Path> get(Long id) {
    return intermediateResults.get(id).stream()
        .map(MemoryManager::readPartition)
        .flatMap(list -> list.stream())
        .collect(Collectors.toList());
  }

  private static void writePartition(Long id, List<Path> partition) {

    if (partition.isEmpty())
      return;

    try {
      // Create the temp file
      File temp = File.createTempFile("partition_" + maxId++ + "_", ".tmp");
      temp.deleteOnExit(); // delete on JVM exit

      // Add this file to our intermediateResults
      List<File> fileHandles = intermediateResults.getOrDefault(id, new ArrayList<File>());
      fileHandles.add(temp);
      intermediateResults.put(id, fileHandles);

      // Write our partition into the file as a byte[]
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp));
      oos.writeObject(Path.serialize(partition));
      oos.flush();

      // Done
      System.out.println("Partition written to: " + temp.getAbsolutePath());

    } catch (Exception e) {
      System.out.println("Problem serializing: " + e);
    }
  }

  private static List<Path> readPartition(File file) {

    try {
      FileInputStream in = new FileInputStream(file);
      ObjectInputStream ois = new ObjectInputStream(in);
      List<Path> result = Path.deserialize((byte[]) ois.readObject());
      return result;
    } catch (Exception e) {
      System.out.println("Problem deserializing: " + e);
      System.out.println("File: " + file.getAbsolutePath());
    }
    return null;
  }
}