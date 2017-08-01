/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.physicallibrary;

import com.github.giedomak.telepathdb.datamodels.Path;
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore;
import com.github.giedomak.telepathdb.datamodels.utilities.Logger;
import com.github.giedomak.telepathdb.memorymanager.MemoryManager;

import java.util.stream.Stream;

/**
 * Created by giedomak on 03/03/2017.
 */
public class HashJoin {

  /**
   * Join two streams of Paths following the HashJoin algorithm and by using our MemoryManager.
   *
   * @param stream1 First stream of paths we'll join on its lastNode()
   * @param stream2 Second stream of paths we'll join on its firstNode()
   * @return A stream with the concatenated paths of stream1 and stream2
   */
  public static Stream<Path> run(Stream<Path> stream1, Stream<Path> stream2) {

    // Make sure we get a free slot in the MemoryManager
    long offset = MemoryManager.getMaxId() + 1;

    // Put all Paths from stream1 into a HashMap with the lastNode() as key
    stream1.forEach(path -> MemoryManager.put(offset + path.lastNode().getId(), path));

    Logger.INSTANCE.debug("Done creating the hashTable, now concatenating");

    // Get all Paths from the HashMap which have the fristNode() as key, and concatenate
    return stream2.flatMap(v2 -> MemoryManager.get(offset + v2.firstNode().getId())
        .map(v1 -> PathIdentifierStore.INSTANCE.concatenatePaths(v1, v2)));

  }
}
