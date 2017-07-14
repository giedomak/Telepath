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

import static org.junit.Assert.assertEquals;

/**
 * Created by giedomak on 22/02/2017.
 */
public class PathTest {

  // Path invalid because should have at least two nodes --> throw error
  @Test(expected = java.lang.IllegalArgumentException.class)
  public void throwsErrorWithEmptyNodesList() {
    Path path = new Path(3, new ArrayList<Node>());
  }

  // Path invalid because should have at least two nodes --> throw error
  @Test(expected = java.lang.IllegalArgumentException.class)
  public void throwsErrorWithOneNode() {
    Path path = new Path(3, createNodeList(1));
  }

  @Test
  public void createsPath() {
    Path path = new Path(3, createNodeList(2));
    assertEquals("Should have created the path with the same id", 3, path.pathId);
  }

  @Test
  public void returnsFirstNode() {
    List<Node> nodes = createNodeList(3);
    Path path = new Path(3, nodes);
    assertEquals("should return the first node of the nodes array", nodes.get(0), path.firstNode());
  }

  @Test
  public void returnsLastNode() {
    List<Node> nodes = createNodeList(3);
    Path path = new Path(3, nodes);
    assertEquals("should return the last node of the nodes array", nodes.get(2), path.lastNode());
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
