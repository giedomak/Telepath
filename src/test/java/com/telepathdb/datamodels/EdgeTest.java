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
public class EdgeTest {

  @Test
  public void outputsToString() {
    // given
    Edge edge = new Edge("GMack");
    String output = "Edge(" + "label=" + edge.getLabel() + ")";

    // then
    assertEquals(output, edge.toString());
  }

  @Test
  public void generatesSameHashCode() {
    // given
    Edge edge1 = new Edge("Beast");
    Edge edge2 = new Edge("Beast");
    Edge edge3 = new Edge("Barbie");

    // then
    assertEquals(edge1.hashCode(), edge2.hashCode());
    assertNotEquals(edge1.hashCode(), edge3.hashCode());
  }
}
