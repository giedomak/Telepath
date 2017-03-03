package com.telepathdb.physicallibrary;

import com.telepathdb.datamodels.Node;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.stores.PathIdentifierStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by giedomak on 03/03/2017.
 */
public class HashJoin {

  static Stream<Path> run(Stream<Path> stream1, Stream<Path> stream2) {

    List<Path> result = new ArrayList<>();
    Map<Node, List<Path>> map = new HashMap<>();

    // Put all Paths from stream1 into a HashMap with the lastNode() as key
    stream1.forEach(path -> {
      List<Path> v = map.getOrDefault(path.lastNode(), new ArrayList<>());
      v.add(path);
      map.put(path.lastNode(), v);
    });

    // Get all Paths from the HashMap which have the fristNode() as key, and concatenate
    stream2.forEach(path -> {
      List<Path> lst = map.get(path.firstNode());
      if (lst != null) {
        lst.stream().forEach(r -> {
          result.add(PathIdentifierStore.concatenatePathsAndStore(r, path));
        });
      }
    });

    return result.stream();
  }
}
