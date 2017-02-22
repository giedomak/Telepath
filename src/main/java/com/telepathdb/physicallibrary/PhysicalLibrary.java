/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.physicallibrary;

import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.stores.PathIdentifierStore;
import com.telepathdb.memorymanager.MemoryManager;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The physical operators we support
 */
final public class PhysicalLibrary {

  final static private boolean defaultDistinct = true;

  // ------------- UNION ---------------

  public static Stream<Path> union(List<Path> list1, List<Path> list2) {
    return union(list1.stream(), list2.stream(), defaultDistinct);
  }

  public static Stream<Path> union(Stream<Path> stream1, Stream<Path> stream2) {
    return union(stream1, stream2, defaultDistinct);
  }

  public static Stream<Path> union(Stream<Path> stream1, Stream<Path> stream2, boolean distinct) {
    System.out.println(stream1.isParallel());
    System.out.println(stream2.isParallel());
    // Out-of-the-box Java 8 Streams
    if (distinct) {
      return Stream.concat(stream1, stream2).distinct();
    } else {
      return Stream.concat(stream1, stream2);
    }
  }

  // ------------- CONCATENATION ---------------

  public static Stream<Path> concatenation(Stream<Path> stream1, Stream<Path> stream2) {

    // Because we are doing a nested loop, we have to create the stream again for each new iteration.
    Supplier<Stream<Path>> streamSupplier = MemoryManager.streamSupplier(stream2);

    // Basically we are doing a nested loop to do an inner-join and concatentate the paths.
    return stream1.flatMap(v1 -> streamSupplier.get()
        .filter(v2 -> v1.lastNode().equals(v2.firstNode()))
        .map(v2 -> PathIdentifierStore.concatenatePathsAndStore(v1, v2)));
  }
}
