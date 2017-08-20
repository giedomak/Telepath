/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.graph.Edge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class EdgeTest {

    @Test
    fun outputsToString() {
        // given
        val edge = Edge("GMack")
        val output = "Edge(" + "label=" + edge.label + ")"

        // then
        assertEquals(output, edge.toString())
    }

    @Test
    fun generatesSameHashCode() {
        // given
        val edge1 = Edge("Beast")
        val edge2 = Edge("Beast")
        val edge3 = Edge("Barbie")

        // then
        assertEquals(edge1.hashCode().toLong(), edge2.hashCode().toLong())
        assertNotEquals(edge1.hashCode().toLong(), edge3.hashCode().toLong())
    }
}
