/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.kpathindex;

import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathPrefix;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * KPathIndex interface defining the public methods of the kpathindex package
 */
public interface KPathIndex {

  /**
   * Search method to lookup paths in the KPathIndex
   *
   * @param pathPrefix The prefix of a path which we need to search
   * @return An Iterable with Paths which satisfy the pathPrefix
   * @throws IOException I/O error
   */
  Stream<Path> search(PathPrefix pathPrefix);

  /**
   * Insert method to insert a Path into the KPathIndex
   *
   * @param path The path we will insert into the KPathIndex
   */
  void insert(Path path);

  /**
   * Getter for K
   * K indicates the paths with up to this K number of edges are indexed in the kPathIndex
   * For example: if K=2, the index contains all paths which have 1 or 2 edges along the path.
   *
   * @return The value for K up to which the index has paths included in its index.
   */
  int getK();

  /**
   * Setter for K
   *
   * @param k The new value for K
   */
  void setK(int k);

}
