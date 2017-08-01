/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.integrationtests;

import com.pathdb.pathIndex.Node;
import com.pathdb.pathIndex.Path;
import com.pathdb.pathIndex.PathIndex;
import com.pathdb.pathIndex.PathPrefix;
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PathDBIT {
  @Test
  public void pathIndexIntegrationTest() throws Exception {
    // given
    PathIndex index = new InMemoryIndexFactory().getInMemoryIndex();

    // when
    ArrayList<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1));
    nodes.add(new Node(2));
    nodes.add(new Node(3));
    Path path = new Path(42, nodes);
    index.insert(path);

    // then
    Iterable<Path> paths = index.getPaths(new PathPrefix(42, 3));
    Iterator<Path> iterator = paths.iterator();
    Path next = iterator.next();
    assertEquals("Should have found the same path in the index.", path, next);
    assertFalse(iterator.hasNext());
  }
}
