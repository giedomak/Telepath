package com.telepathdb.physicallibrary;


import com.telepathdb.datamodels.Path;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by giedomak on 06/02/2017.
 */
public class MergeJoin {

  public static Stream<Path> perform(Iterable<Path> sortedPaths1, Iterable<Path> sortedPaths2) {

    Stream<Path> stream1 = StreamSupport.stream(sortedPaths1.spliterator(), false);
    Stream<Path> stream2 = StreamSupport.stream(sortedPaths2.spliterator(), false);

    return perform(stream1, stream2);
  }

  public static Stream<Path> perform(Stream<Path> stream1, Stream<Path> stream2) {

    return Stream.concat(stream1, stream2);
  }
}
