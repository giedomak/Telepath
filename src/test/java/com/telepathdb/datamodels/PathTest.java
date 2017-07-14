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

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  // ---------- EQUALS ---------

  @Test
  public void equalsToSameObject() {
    Path path = new Path(3, createNodeList(2));

    assertTrue("should return true for the same object", path.equals(path));
  }

  @Test
  public void equalsFalseForOtherObject() {
    Path path = new Path(3, createNodeList(2));

    assertFalse("should return false for an object from another class", path.equals(new Node(3)));
    assertFalse("should return false for null", path.equals(null));
  }

  @Test
  public void equalsTrueForPathWithSameCharacteristics() {
    List<Node> nodes = createNodeList(2);
    Path path1 = new Path(3, nodes);
    Path path2 = new Path(3, nodes);

    assertTrue("should return true for a Path with the same characteristics", path1.equals(path2));
  }

  // TODO: another nodes list, but with nodes with the same characteristics

  // ---------- HELPERS ---------

  private List<Node> createNodeList(int size) {
    List<Node> nodes = new ArrayList<>(size);
    IntStream.generate(() -> ThreadLocalRandom.current().nextInt(10))
        .limit(size)
        .forEach(random -> nodes.add(new Node(random)));

    return nodes;
  }
}
