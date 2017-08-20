/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.IntStream

/**
 * Created by giedomak on 22/02/2017.
 */
class PathTest {

    // Path invalid because should have at least two nodes --> throw error
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun throwsErrorWithEmptyNodesList() {
        Path(3, ArrayList<Node>())
    }

    // Path invalid because should have at least two nodes --> throw error
    @Test(expected = java.lang.IllegalArgumentException::class)
    fun throwsErrorWithOneNode() {
        Path(3, createNodeList(1))
    }

    @Test
    fun createsPath() {
        val path = Path(3, createNodeList(2))
        assertEquals("Should have created the path with the same id", 3, path.pathId)
    }

    // ---------- EQUALS ---------

    @Test
    fun samePathsEqualEachOtherTest() {
        // given
        val a = Path(42, equalNodes(4, 42))
        val b = Path(42, equalNodes(4, 42))

        // then
        assertEquals(a, a)
        assertEquals(a, b)
    }

    // TODO: another nodes list, but with nodes with the same characteristics

    // ---------- METHODS ---------

    @Test
    fun lengthReturnsNodesSize() {
        // given
        val a = Path(42, equalNodes(3, 42))
        val b = Path(42, equalNodes(4, 24))

        // then
        assertEquals(3, a.length.toLong())
        assertEquals(4, b.length.toLong())
    }

    @Test
    fun differentPathsAreNotEqualsTest() {
        // given
        val a = Path(42, equalNodes(4, 42))
        val b = Path(42, equalNodes(4, 24))
        val c = Path(42, equalNodes(3, 42))

        val differentNodes = equalNodes(3, 42)
        differentNodes.removeAt(differentNodes.size - 1)
        differentNodes.add(Node(43))

        val d = Path(42, differentNodes)

        // then
        assertFalse(a == b)
        assertFalse(b == a)
        assertFalse(a == c)
        assertFalse(c == a)
        assertFalse(c == d)
        assertFalse(d == c)
    }

    @Test
    fun generatesSameHashCode() {
        // given
        val path1 = Path(42, equalNodes(4, 42))
        val path2 = Path(42, equalNodes(4, 42))
        val path3 = Path(42, equalNodes(4, 44))
        val path4 = Path(43, equalNodes(4, 44))

        // then
        assertEquals(path1.hashCode().toLong(), path2.hashCode().toLong())
        assertNotEquals(path1.hashCode().toLong(), path3.hashCode().toLong())
        assertNotEquals(path1.hashCode().toLong(), path4.hashCode().toLong())
    }

    @Test
    fun outputsToString() {
        // given
        val a = Path(42, equalNodes(4, 42))
        val output = "Path(" + "pathId=" + a.pathId + ", nodes=" + a.nodes + ")"

        // then
        assertEquals(output, a.toString())
    }

    // ---------- HELPERS ---------

    private fun createNodeList(size: Int): List<Node> {
        val nodes = ArrayList<Node>(size)
        IntStream.generate { ThreadLocalRandom.current().nextInt(10) }
                .limit(size.toLong())
                .forEach { random -> nodes.add(Node(random.toLong())) }

        return nodes
    }

    companion object {

        /**
         * Generate a list of nodes which all have the same ID.
         *
         * @param count Number of nodes to generate.
         * @param id    ID the nodes will be given.
         * @return List of nodes with the same ID.
         */
        fun equalNodes(count: Int, id: Long): MutableList<Node> {
            return (1..count).mapTo(mutableListOf()) { Node(id) }
        }

        fun simplePath(pathID: Long, length: Int, value: Long?): Path {
            val nodes = ArrayList<Node>(length)

            for (i in 0..length - 1) {
                nodes.add(Node(value!!))
            }

            return Path(pathID, nodes)
        }

        fun increasingNodes(count: Int, startingId: Long): List<Node> {
            return (startingId..(startingId + count - 1)).map { Node(it) }
        }
    }
}
