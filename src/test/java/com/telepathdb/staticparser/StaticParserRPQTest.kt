/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser

import com.telepathdb.datamodels.ParseTree
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
        assertEquals(createSimpleParseTree("a"), actual)
    }

    @Test
    fun concatenationInput() {
        // Parse the input into a ParseTree
        val actual = StaticParserRPQ.parse("a/b")

        // Create the expected ParseTree
        val a = create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "b"))
        val b = create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "c"))
        val c = create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("b", "b"))
        val d = create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "b", "c"))

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
        val a = create1LevelParseTree(ParseTree.UNION, Arrays.asList("a", "b"))
        val b = create1LevelParseTree(ParseTree.UNION, Arrays.asList("a", "c"))
        val c = create1LevelParseTree(ParseTree.UNION, Arrays.asList("b", "b"))
        val d = create1LevelParseTree(ParseTree.UNION, Arrays.asList("a", "b", "c"))

        // Then
        assertEquals(a, actual)
        assertNotEquals(b, actual)
        assertNotEquals(c, actual)
        assertNotEquals(d, actual)
    }

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // ParseTree without children, just a leaf.
        fun createSimpleParseTree(label: String): ParseTree {
            val parseTree = ParseTree(true)
            parseTree.setLeaf(label)
            return parseTree
        }

        // ParseTree with 1 level of children, root will get the operator param.
        fun create1LevelParseTree(operator: Int, labels: List<String>): ParseTree {
            val parseTree = ParseTree(true)
            parseTree.setOperator(operator)

            // Create the children and add them to the root
            for (i in labels.indices) {
                val child = ParseTree()
                child.setLeaf(labels[i])
                parseTree.setChild(i, child)
            }

            return parseTree
        }
    }
}
