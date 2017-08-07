package com.github.giedomak.telepathdb.datamodels.parsetree

import org.junit.Test
import kotlin.test.assertEquals

class ParseTreeUnionPullerTest {

    @Test
    fun pullsOutUnionsIntoMultipleParseTrees() {

        // Given
        val input = exampleUnionParseTree()
        val actual = ParseTreeUnionPuller.parse(input)

        // Create expected parseTree
        //       CONCATENATION
        //        /      \
        //  CONCATENATION  d
        //      /   \
        //     a     b
        val child1 = ParseTreeTest.create1LevelParseTree(
                ParseTree.CONCATENATION, listOf("a", "b"), false)
        val root1 = ParseTreeTest.create1LevelParseTree(
                ParseTree.CONCATENATION, listOf("d"))
        root1.children.add(0, child1)

        // Create expected parseTree
        //       CONCATENATION
        //        /      \
        //  CONCATENATION  d
        //      /   \
        //     a     c
        val child2 = ParseTreeTest.create1LevelParseTree(
                ParseTree.CONCATENATION, listOf("a", "c"), false)
        val root2 = ParseTreeTest.create1LevelParseTree(
                ParseTree.CONCATENATION, listOf("d"))
        root2.children.add(0, child2)

        assertEquals(listOf(root1, root2), actual)
    }

    @Test
    fun splitsParseTreesWhenRootIsUnion() {

        // Given:
        //     UNION
        //      / \
        //     a   b
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("a", "b"))
        val actual = ParseTreeUnionPuller.parse(input)

        // Generate expected
        val a = ParseTreeTest.createSimpleParseTree("a")
        val b = ParseTreeTest.createSimpleParseTree("b")

        assertEquals(listOf(a, b), actual)
    }

    private fun exampleUnionParseTree(): ParseTree {

        // Your input: a/(b|c)/d
        //
        //        CONCATENATION[2]
        //       / \
        //      /   \
        //     /     \
        //    /       \
        //    CONCATENATION[2]       d
        //   / \
        //  /   \
        //  a   UNION[2]
        //       / \
        //       b c
        val child2 = ParseTreeTest.create1LevelParseTree(
                ParseTree.UNION, listOf("b", "c"), false)
        val child1 = ParseTreeTest.create1LevelParseTree(
                ParseTree.CONCATENATION, listOf("a"), false)
        child1.setChild(1, child2)

        val root = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("d"))
        root.children.add(0, child1)

        return root
    }
}
