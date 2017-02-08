package com.telepathdb.physicallibrary;

import com.telepathdb.datamodels.Path;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by giedomak on 08/02/2017.
 */
final public class PhysicalLibrary {

  final static private boolean defaultDistinct = true;

  public static Stream<Path> union(Iterable<Path> iterator1, Iterable<Path> iterator2) {

    Stream<Path> stream1 = StreamSupport.stream(iterator1.spliterator(), false);
    Stream<Path> stream2 = StreamSupport.stream(iterator2.spliterator(), false);

    return union(stream1, stream2, defaultDistinct);
  }

  public static Stream<Path> union(Stream<Path> stream1, Stream<Path> stream2) {
    return union(stream1, stream2, defaultDistinct);
  }

  public static Stream<Path> union(Stream<Path> stream1, Stream<Path> stream2, boolean distinct) {
    if (distinct) {
      return Stream.concat(stream1, stream2).distinct();
    } else {
      return Stream.concat(stream1, stream2);
    }
  }
}
