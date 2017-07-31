/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.utilities

import com.telepathdb.datamodels.ParseTree
import com.telepathdb.staticparser.StaticParserRPQTest
import org.junit.Test
import kotlin.test.assertEquals

class ParseTreeFlattenerTest {

    @Test
    fun flattensConcatenation() {

        // Given:
        //       CONCATENATION
        //          /    \
        //         a    CONCATENATION
        //                 /    \
        //                b      c
        val child = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("b", "c"), false)
        val root = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a"))
        root.setChild(1, child)

        // Expected:
        //       CONCATENATION
        //          /  |  \
        //         a   b   c
        val expected = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "c"))

        // Then
        assertEquals(expected, ParseTreeFlattener.flatten(root))
    }

    @Test
    fun flattensComplexConcatenation() {

        // Given:
        //       CONCATENATION
        //          /    \
        //         a    CONCATENATION
        //                 /      \
        //        CONCATENATION    d
        //           /    \
        //          b      c
        val child1 = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("b", "c"), false)
        val child2 = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("d"), false)
        child2.children.add(0, child1)
        val root = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a"))
        root.setChild(1, child2)

        // Expected:
        //        CONCATENATION
        //          /  |  |  \
        //         a   b  c   c
        val expected = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "c", "d"))

        // Then
        assertEquals(expected, ParseTreeFlattener.flatten(root))
    }

    @Test
    fun flattensComplexOperatorCombination() {

        // Given:
        //            CONCATENATION
        //          /       |        \
        //         a  CONCATENATION   f
        //              /      \
        //          UNION       e
        //         /    \
        //        b    UNION
        //             /  \
        //            c    d
        val child1 = StaticParserRPQTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"), false)
        val child2 = StaticParserRPQTest.create1LevelParseTree(ParseTree.UNION, listOf("b"), false)
        child2.children.add(child1)
        val child3 = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("e"), false)
        child3.children.add(0, child2)
        val root = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "f"))
        root.children.add(1, child3)

        // Expected:
        //        CONCATENATION
        //        /   |    |  \
        //       a  UNION  e   f
        //          / | \
        //         b  c  d
        val child = StaticParserRPQTest.create1LevelParseTree(ParseTree.UNION, listOf("b", "c", "d"), false)
        val expected = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "e", "f"))
        expected.children.add(1, child)

        // Then
        assertEquals(expected, ParseTreeFlattener.flatten(root))
    }
}
