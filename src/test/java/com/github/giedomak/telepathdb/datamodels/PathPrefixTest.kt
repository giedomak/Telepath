/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*

class PathPrefixTest {

    @Test
    fun samePathPrefixPrefixesEqualEachOtherTest() {
        // given
        val pathId = PathIdentifierStore.getPathIdByEdgeLabel(Arrays.asList("a", "b", "c"))
        val a = PathPrefix(pathId, PathTest.equalNodes(4, "42"))
        val b = PathPrefix(pathId, PathTest.equalNodes(4, "42"))

        // then
        assertEquals(a, a)
        assertEquals(a, b)
    }

    @Test
    fun differentPathPrefixPrefixesAreNotEqualsTest() {
        // given
        val a = PathPrefix(42, PathTest.equalNodes(4, "42"))
        val b = PathPrefix(42, PathTest.equalNodes(4, "24"))
        val c = PathPrefix(42, PathTest.equalNodes(3, "42"))
        val d = PathPrefix(43, PathTest.equalNodes(3, "42"))

        val differentNodes = PathTest.equalNodes(3, "42")
        differentNodes.removeAt(differentNodes.size - 1)
        differentNodes.add(Node("43"))

        val e = PathPrefix(42, differentNodes)

        // then
        assertNotEquals(a, b)
        assertNotEquals(a, d)
        assertNotEquals(c, d)
        assertNotEquals(b, a)
        assertNotEquals(a, c)
        assertNotEquals(c, a)
        assertNotEquals(c, e)
        assertNotEquals(e, c)
    }

    // ---------- METHODS ---------

    @Test
    fun generatesHashCode() {
        // given
        val a = PathPrefix(42, PathTest.equalNodes(4, "42"))
        val b = PathPrefix(42, PathTest.equalNodes(4, "42"))
        val c = PathPrefix(42, PathTest.equalNodes(4, "24"))

        // then
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a.hashCode(), c.hashCode())
    }

    @Test
    fun outputsToString() {
        // given
        val a = PathPrefix(31)
        val output = "PathPrefix(pathId=31, nodes=" + a.nodes + ")"

        // then
        assertEquals(a.toString(), output)
    }

}
