/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the pathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package pathdb.kpathindex;

import com.pathdb.pathIndex.Path;
import com.pathdb.pathIndex.PathPrefix;

import java.io.IOException;

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
  Iterable<Path> search(PathPrefix pathPrefix) throws IOException;

  /**
   * Insert method to insert a Path into the KPathIndex
   *
   * @param path The path we will insert into the KPathIndex
   */
  void insert(Path path);

}
