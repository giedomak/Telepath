/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.kpathindex;

import com.telepathdb.datamodels.Node;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathPrefix;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;

public class KPathIndexTest {

  private KPathIndex kPathIndex;

  @Before
  public void beforeEachTest() {
    // We are testing an interface, so we are mocking the interface
    kPathIndex = Mockito.mock(KPathIndex.class);
  }

  @Test
  public void searchMethod() throws IOException {
    PathPrefix pathPrefix = new PathPrefix(4, 8);
    kPathIndex.search(pathPrefix);
    // Make sure the search method was called with the right argument
    Mockito.verify(kPathIndex).search(pathPrefix);
  }

  @Test
  public void insertMethod() {
    Path path = new Path(3, new ArrayList<Node>());
    kPathIndex.insert(path);
    // Make sure the insert method was called with the right argument
    Mockito.verify(kPathIndex).insert(path);
  }
}
