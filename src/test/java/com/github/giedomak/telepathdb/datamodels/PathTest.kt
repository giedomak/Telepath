/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
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
        val a = Path(42, equalNodes(4, "42"))
        val b = Path(42, equalNodes(4, "42"))

        // then
        assertEquals(a, a)
        assertEquals(a, b)
    }

    @Test
    fun differentPathsAreNotEqualsTest() {
        // given
        val a = Path(42, equalNodes(4, "42"))
        val b = Path(42, equalNodes(4, "24"))
        val c = Path(42, equalNodes(3, "42"))

        val differentNodes = equalNodes(3, "42")
        differentNodes.removeAt(differentNodes.size - 1)
        differentNodes.add(Node("43"))

        val d = Path(42, differentNodes)
        val e = Path(43, equalNodes(4, "42"))

        // then
        assertNotEquals(a, b)
        assertNotEquals(b, a)
        assertNotEquals(a, c)
        assertNotEquals(c, a)
        assertNotEquals(c, d)
        assertNotEquals(d, c)
        assertNotEquals(a, e)
    }

    @Test
    fun generatesSameHashCode() {
        // given
        val path1 = Path(42, equalNodes(4, "42"))
        val path2 = Path(42, equalNodes(4, "42"))
        val path3 = Path(42, equalNodes(4, "44"))
        val path4 = Path(43, equalNodes(4, "44"))

        // then
        assertEquals(path1.hashCode(), path2.hashCode())
        assertNotEquals(path1.hashCode(), path3.hashCode())
        assertNotEquals(path1.hashCode(), path4.hashCode())
    }

    @Test
    fun outputsToString() {
        // given
        val a = Path(42, equalNodes(4, "42"))
        val output = "Path(" + "pathId=" + a.pathId + ", nodes=" + a.nodes + ")"

        // then
        assertEquals(output, a.toString())
    }

    // ---------- HELPERS ---------

    private fun createNodeList(size: Int): List<Node> {
        val nodes = ArrayList<Node>(size)
        IntStream.generate { ThreadLocalRandom.current().nextInt(10) }
                .limit(size.toLong())
                .forEach { random -> nodes.add(Node(random.toString())) }

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
        fun equalNodes(count: Int, label: String): MutableList<Node> {
            return (1..count).mapTo(mutableListOf()) { Node(label) }
        }

        fun simplePath(pathID: Long, length: Int, label: String): Path {
            val nodes = ArrayList<Node>(length)

            for (i in 0 until length) {
                nodes.add(Node(label))
            }

            return Path(pathID, nodes)
        }

        fun increasingNodes(count: Int, startingId: Long): List<Node> {
            return (startingId..(startingId + count - 1)).map { Node(it.toString()) }
        }
    }
}
