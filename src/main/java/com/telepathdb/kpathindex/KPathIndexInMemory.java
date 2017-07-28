/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.kpathindex;

import com.pathdb.pathIndex.PathIndex;
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathPrefix;
import com.telepathdb.datamodels.integrations.PathDBWrapper;
import com.telepathdb.memorymanager.spliterator.FixedBatchSpliterator;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * InMemory implementation of the KPathIndex
 */
public class KPathIndexInMemory implements KPathIndex {

  private PathIndex pathIndex;
  private int k;

  /**
   * Constructor which populates our pathIndex variable with the InMemoryIndex obtained from the
   * InMemoryIndexFactory from the com.pathdb package
   */
  public KPathIndexInMemory() {
    this.pathIndex = new InMemoryIndexFactory().getInMemoryIndex();
  }

  /**
   * Search method to lookup paths in the KPathIndex
   *
   * @param pathPrefix The prefix of a path which we need to search
   * @return A Stream with Paths which satisfy the pathPrefix
   */
  @Override
  public Stream<Path> search(PathPrefix pathPrefix) {
    // We have to cast the Path model from pathDB's one, to our own again
    try {
      return StreamSupport.stream(
          new FixedBatchSpliterator<>(
              pathIndex.getPaths(
                  PathDBWrapper.toPathPrefix(pathPrefix)).spliterator()), true
      ).map(PathDBWrapper::fromPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Stream.empty();
  }

  /**
   * Insert method to insert a Path into the KPathIndex
   *
   * @param path The path we will insert into the KPathIndex
   */
  @Override
  public void insert(Path path) {
    pathIndex.insert(PathDBWrapper.toPath(path));
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }
}
