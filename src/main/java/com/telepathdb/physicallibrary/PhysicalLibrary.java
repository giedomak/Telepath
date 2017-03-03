/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.physicallibrary;

import com.telepathdb.datamodels.Path;

import java.util.List;
import java.util.stream.Stream;

/**
 * The physical operators we support
 */
final public class PhysicalLibrary {

  final static private boolean DEFAULT_DISTINCT = true;

  // ------------- UNION ---------------

  public static Stream<Path> union(List<Path> list1, List<Path> list2) {
    return union(list1.stream(), list2.stream(), DEFAULT_DISTINCT);
  }

  public static Stream<Path> union(Stream<Path> stream1, Stream<Path> stream2) {
    return union(stream1, stream2, DEFAULT_DISTINCT);
  }

  public static Stream<Path> union(Stream<Path> stream1, Stream<Path> stream2, boolean distinct) {
    // Out-of-the-box Java 8 Streams
    if (distinct) {
      return Stream.concat(stream1, stream2).distinct().parallel();
    } else {
      return Stream.concat(stream1, stream2).parallel();
    }
  }

  // ------------- CONCATENATION ---------------

  public static Stream<Path> concatenation(Stream<Path> stream1, Stream<Path> stream2) {
    return HashJoin.run(stream1, stream2);
  }
}
