/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.staticparser

import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTreeTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*

class StaticParserRPQTest {

    @Test
    fun inputBecomesAParseTree() {
        // Parse the input into a ParseTree
        val actual = StaticParserRPQ.parse("a")

        // Then
        assertEquals(ParseTreeTest.createSimpleParseTree("a"), actual)
    }

    @Test
    fun concatenationInput() {
        // Parse the input into a ParseTree
        val actual = StaticParserRPQ.parse("a/b")

        // Create the expected ParseTree
        val a = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "b"))
        val b = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "c"))
        val c = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("b", "b"))
        val d = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "b", "c"))

        // Then
        assertEquals(a, actual)
        assertNotEquals(b, actual)
        assertNotEquals(c, actual)
        assertNotEquals(d, actual)
    }

    @Test
    fun unionInput() {
        // Parse the input into a ParseTree
        val actual = StaticParserRPQ.parse("a|b")

        // Create the expected ParseTree
        val a = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, Arrays.asList("a", "b"))
        val b = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, Arrays.asList("a", "c"))
        val c = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, Arrays.asList("b", "b"))
        val d = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, Arrays.asList("a", "b", "c"))

        // Then
        assertEquals(a, actual)
        assertNotEquals(b, actual)
        assertNotEquals(c, actual)
        assertNotEquals(d, actual)
    }
}
