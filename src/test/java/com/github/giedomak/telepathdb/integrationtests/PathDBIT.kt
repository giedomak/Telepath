/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.integrationtests

import com.pathdb.pathIndex.Node
import com.pathdb.pathIndex.Path
import com.pathdb.pathIndex.PathPrefix
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.*

class PathDBIT {

    @Test
    fun pathIndexIntegrationTest() {
        // given
        val index = InMemoryIndexFactory().inMemoryIndex

        // when
        val nodes = ArrayList<Node>()
        nodes.add(Node(1))
        nodes.add(Node(2))
        nodes.add(Node(3))
        val path = Path(42, nodes)
        index.insert(path)

        // then
        val paths = index.getPaths(PathPrefix(42, 3))
        val iterator = paths.iterator()
        val next = iterator.next()
        assertEquals("Should have found the same path in the index.", path, next)
        assertFalse(iterator.hasNext())
    }
}
