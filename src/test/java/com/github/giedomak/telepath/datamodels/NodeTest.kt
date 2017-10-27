/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.datamodels

import com.github.giedomak.telepath.datamodels.graph.Node
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NodeTest {

    @Test
    fun outputsToString() {
        // given
        val node = Node("33")
        val expected = "Node(" + "label=33)"

        // then
        assertEquals(expected, node.toString())
    }

    @Test
    fun generatesSameHashCode() {
        // given
        val node1 = Node("42")
        val node2 = Node("42")
        val node3 = Node("46")

        // then
        assertEquals(node1.hashCode(), node2.hashCode())
        assertNotEquals(node1.hashCode(), node3.hashCode())
    }
}
