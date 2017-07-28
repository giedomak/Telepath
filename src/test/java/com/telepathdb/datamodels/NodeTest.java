/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by giedomak on 26/07/2017.
 */
public class NodeTest {

  @Test
  public void outputsToString() {
    // given
    Node node = new Node(33);
    String expected = "Node(" + "id=" + node.getId() + ")";

    // then
    assertEquals(expected, node.toString());
  }

  @Test
  public void generatesSameHashCode() {
    // given
    Node node1 = new Node(42);
    Node node2 = new Node(42);
    Node node3 = new Node(46);

    // then
    assertEquals(node1.hashCode(), node2.hashCode());
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }
}
