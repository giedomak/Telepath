/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.integrationtests

import com.google.common.io.Files
import com.jakewharton.byteunits.BinaryByteUnit
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory
import com.pathdb.pathIndex.models.ImmutablePath
import com.pathdb.pathIndex.models.ImmutablePathPrefix
import com.pathdb.pathIndex.models.Node
import com.pathdb.pathIndex.persisted.LMDBIndexFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.*

class PathDBIT {

    @Test
    fun pathIndexInMemoryIntegrationTest() {
        // given
        val index = InMemoryIndexFactory().index

        // when
        val nodes = ArrayList<Node>()
        nodes.add(Node(1))
        nodes.add(Node(2))
        nodes.add(Node(3))
        val path = ImmutablePath.of(42, nodes)
        index.insert(path)

        // then
        val paths = index.getPaths(ImmutablePathPrefix.of(42, emptyList()))
        val iterator = paths.iterator()
        val next = iterator.next()
        assertEquals("Should have found the same path in the index.", path, next)
        assertFalse(iterator.hasNext())
    }

    @Test
    fun pathIndexDiskIntegrationTest() {
        // given
        val dir = Files.createTempDir()
        val index = LMDBIndexFactory(dir).withMaxDBSize(1, BinaryByteUnit.GIBIBYTES).build()

        // when
        val nodes = ArrayList<Node>()
        nodes.add(Node(1))
        nodes.add(Node(2))
        nodes.add(Node(3))
        val path = ImmutablePath.of(42, nodes)
        index.insert(path)

        // then
        val paths = index.getPaths(ImmutablePathPrefix.of(42, emptyList()))
        val iterator = paths.iterator()
        val next = iterator.next()
        assertEquals("Should have found the same path in the index.", path, next)
        assertFalse(iterator.hasNext())
    }
}
