/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by giedomak on 22/02/2017.
 */
public class PathTest {

  @Test(expected = java.lang.IllegalArgumentException.class)
  public void throwsErrorWithEmptyNodesList() {
    Path path = new Path(3, new ArrayList<Node>());
  }

  @Test(expected = java.lang.IllegalArgumentException.class)
  public void throwsErrorWithOneNode() {
    Path path = new Path(3, createNodeList(1));
  }

  @Test
  public void createsPath() {
    Path path = new Path(3, createNodeList(2));
  }

  // ---------- HELPERS ---------

  private List<Node> createNodeList(int size) {
    List<Node> nodes = new ArrayList<>(size);
    IntStream.generate(() -> ThreadLocalRandom.current().nextInt(10))
        .limit(size)
        .forEach(random -> nodes.add(new Node(random)));

    return nodes;
  }
}
