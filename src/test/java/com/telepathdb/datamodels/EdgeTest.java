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

import static com.telepathdb.datamodels.PathTest.equalNodes;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

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
}
