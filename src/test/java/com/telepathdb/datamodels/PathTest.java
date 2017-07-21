/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Created by giedomak on 22/02/2017.
 */
public class PathTest {

  /**
   * Generate a list of nodes which all have the same ID.
   *
   * @param count Number of nodes to generate.
   * @param id    ID the nodes will be given.
   * @return List of nodes with the same ID.
   */
  protected static List<Node> equalNodes(int count, long id) {
    List<Node> nodes = new LinkedList<>();
    IntStream.range(0, count).forEach(i -> nodes.add(new Node(id)));
    return nodes;
  }

  // Path invalid because should have at least two nodes --> throw error
  @Test(expected = java.lang.IllegalArgumentException.class)
  public void throwsErrorWithEmptyNodesList() {
    new Path(3, new ArrayList<Node>());
  }

  // Path invalid because should have at least two nodes --> throw error
  @Test(expected = java.lang.IllegalArgumentException.class)
  public void throwsErrorWithOneNode() {
    new Path(3, createNodeList(1));
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
    assertEquals("should return the first node of the nodes list", nodes.get(0), path.firstNode());
  }

  // ---------- EQUALS ---------

  @Test
  public void returnsLastNode() {
    List<Node> nodes = createNodeList(3);
    Path path = new Path(3, nodes);
    assertEquals("should return the last node of the nodes list", nodes.get(2), path.lastNode());
  }

  @Test
  public void samePathsEqualEachOtherTest() {
    // given
    Path a = new Path(42, equalNodes(4, 42));
    Path b = new Path(42, equalNodes(4, 42));

    // then
    assertEquals(a, a);
    assertEquals(a, b);
  }

  // TODO: another nodes list, but with nodes with the same characteristics

  // ---------- METHODS ---------

  @Test
  public void differentPathsAreNotEqualsTest() {
    // given
    Path a = new Path(42, equalNodes(4, 42));
    Path b = new Path(42, equalNodes(4, 24));
    Path c = new Path(42, equalNodes(3, 42));

    List<Node> differentNodes = equalNodes(3, 42);
    differentNodes.remove(differentNodes.size() - 1);
    differentNodes.add(new Node(43));

    Path d = new Path(42, differentNodes);

    // then
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
    assertFalse(a.equals(c));
    assertFalse(c.equals(a));
    assertFalse(c.equals(d));
    assertFalse(d.equals(c));
    assertFalse(d.equals(null)); // NOPMD - We've overridden this method, so test with null
  }

  @Test
  public void generatesHashCode() {
    // given
    Path a = new Path(42, equalNodes(4, 42));
    Path b = new Path(42, equalNodes(4, 42));

    // then
    assertEquals(a.hashCode(), Objects.hash(4, a.nodes));
    assertEquals(a.hashCode(), b.hashCode());
  }

  // ---------- HELPERS ---------

  @Test
  public void outputsToString() {
    // given
    Path a = new Path(42, equalNodes(4, 42));
    String output = "Path{" + "pathId=" + a.pathId + ", edges=" + (a.nodes.size() - 1) + ", nodes=" + a.nodes.size() + ", nodes=" + a.nodes + "}";

    // then
    assertEquals(a.toString(), output);
  }

  private List<Node> createNodeList(int size) {
    List<Node> nodes = new ArrayList<>(size);
    IntStream.generate(() -> ThreadLocalRandom.current().nextInt(10))
        .limit(size)
        .forEach(random -> nodes.add(new Node(random)));

    return nodes;
  }
}
