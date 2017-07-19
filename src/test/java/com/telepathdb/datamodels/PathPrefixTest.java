/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import com.telepathdb.datamodels.stores.PathIdentifierStore;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.telepathdb.datamodels.PathTest.equalNodes;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Created by giedomak on 17/07/2017.
 */
public class PathPrefixTest {

    @Test
    public void samePathPrefixPrefixesEqualEachOtherTest() throws Exception
    {
      // given
      long pathId = PathIdentifierStore.getPathIdentifierByEdgeLabel(Arrays.asList("a", "b", "c"));
      PathPrefix a = new PathPrefix(pathId, 6, equalNodes( 4, 42 ) );
      PathPrefix b = new PathPrefix(pathId, 6, equalNodes( 4, 42 ) );

      // then
      assertEquals( a, a );
      assertEquals( a, b );
    }

    @Test
    public void differentPathPrefixPrefixesAreNotEqualsTest() throws Exception
    {
      // given
      PathPrefix a = new PathPrefix(42, 6, equalNodes( 4, 42 ) );
      PathPrefix b = new PathPrefix(42, 6, equalNodes( 4, 24 ) );
      PathPrefix c = new PathPrefix(42, 6, equalNodes( 3, 42 ) );

      List<Node> differentNodes = equalNodes( 3, 42 );
      differentNodes.remove( differentNodes.size() - 1 );
      differentNodes.add( new Node( 43 ) );

      PathPrefix d = new PathPrefix(42, 6, differentNodes );

      // then
      assertFalse( a.equals( b ) );
      assertFalse( b.equals( a ) );
      assertFalse( a.equals( c ) );
      assertFalse( c.equals( a ) );
      assertFalse( c.equals( d ) );
      assertFalse( d.equals( c ) );
    }

  // ---------- METHODS ---------

  @Test
  public void generatesHashCode()
  {
    // given
    PathPrefix a = new PathPrefix(42, 6, equalNodes( 4, 42 ) );
    PathPrefix b = new PathPrefix(42, 6, equalNodes( 4, 42 ) );
    PathPrefix c = new PathPrefix(42, 6, equalNodes( 4, 24 ) );

    // then
    assertEquals( a.hashCode(), Objects.hash(42, 6, a.nodes));
    assertEquals( a.hashCode(), b.hashCode() );
    assertNotEquals( a.hashCode(), c.hashCode() );
  }

  @Test
  public void outputsToString()
  {
    // given
    long pathId = PathIdentifierStore.getPathIdentifierByEdgeLabel(Arrays.asList("a", "b", "c"));
    PathPrefix a = new PathPrefix(pathId );
    String output = "PathPrefix{" + "pathId=" + a.pathId + ", length=" + a.length + ", edges=" + PathIdentifierStore.getEdgeSet(pathId) + ", nodes=" + a.nodes + "}";

    // then
    assertEquals( a.toString(), output);
  }
}
