/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.parsetree

import org.junit.Assert
import org.junit.Test

class ParseTreeSizesTest {

    @Test
    fun subtreesOfSize2() {
        // Input:
        //       CONCATENATION
        //        /   |   \
        //       a    b  UNION
        //                / \
        //               c   d
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"))
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))
        input.children.add(child)

        // Expected:
        //   CONCATENATION   UNION
        //       /  \         / \
        //      a    b       c   d
        val expected = listOf(
                ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b")),
                ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"))
        )

        val actual = input.subtreesOfSize(2)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize2Complex() {
        // Input:
        //         CONCATENATION
        //        /  |     |  |  \
        //       a  UNION  e  f   g
        //          / | \
        //         b  c  d
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("b", "c", "d"))
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "e", "f", "g"))
        input.children.add(1, child)

        // Expected:
        //     UNION   UNION    CONCATENATION    CONCATENATION
        //      / \     / \         /   \            /   \
        //     b   c   c   d       e     f          f     g
        val expected = listOf(
                ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("b", "c")),
                ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d")),
                ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("e", "f")),
                ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("f", "g"))
        )

        val actual = input.subtreesOfSize(2)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize3() {
        // Input:
        //        CONCATENATION
        //        /  |    |   \
        //       a   b  UNION  d
        //               / \
        //              c   d
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"))
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "d"))
        input.children.add(2, child)

        // Expected:
        //      CONCATENATION    CONCATENATION
        //         /   \             /   \
        //        b   UNION       UNION   d
        //             / \         / \
        //            c   d       c   d
        val expected1 = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("b"))
        expected1.children.add(child)
        val expected2 = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("d"))
        expected2.children.add(0, child)
        val expected = listOf(expected1, expected2)

        val actual = input.subtreesOfSize(3)

        Assert.assertEquals(expected, actual)
    }
}
