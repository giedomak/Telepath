/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NodeTest {

    @Test
    fun outputsToString() {
        // given
        val node = Node(33)
        val expected = "Node(" + "id=" + node.id + ")"

        // then
        assertEquals(expected, node.toString())
    }

    @Test
    fun generatesSameHashCode() {
        // given
        val node1 = Node(42)
        val node2 = Node(42)
        val node3 = Node(46)

        // then
        assertEquals(node1.hashCode().toLong(), node2.hashCode().toLong())
        assertNotEquals(node1.hashCode().toLong(), node3.hashCode().toLong())
    }
}
