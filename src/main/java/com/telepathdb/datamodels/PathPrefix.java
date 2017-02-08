/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import java.util.List;

/**
 * PathPrefix model
 * At the moment, we just extend PathPrefix from PathDB:
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/PathPrefix.java
 */
public class PathPrefix extends com.pathdb.pathIndex.PathPrefix {

  public PathPrefix(long pathId, int length) {

    super(pathId, length);
  }

  public PathPrefix(long pathId, int length, List<Node> nodes) {
    // Convert our own Node model back to the Node model from PathDB
    super(pathId, length, (List<com.pathdb.pathIndex.Node>) (List<?>) nodes);
  }
}
