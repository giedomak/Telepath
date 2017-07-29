package com.telepathdb.staticparser

import com.telepathdb.datamodels.ParseTree
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class UnionPullerTest {

    @Test
    fun pullsOutUnionsIntoMultipleParseTrees() {

        // Given
        val input = exampleUnionParseTree()
        val actual = UnionPuller.parse(input)

        // Create expected parseTree
        //       CONCATENATION
        //        /      \
        //  CONCATENATION  d
        //      /   \
        //     a     b
        val child1 = StaticParserRPQTest.Companion.create1LevelParseTree(
                ParseTree.CONCATENATION, Arrays.asList("a", "b"))
        val root1 = StaticParserRPQTest.Companion.create1LevelParseTree(
                ParseTree.CONCATENATION, Arrays.asList("d"))
        root1.children!!.add(0, child1)

        // Create expected parseTree
        //       CONCATENATION
        //        /      \
        //  CONCATENATION  d
        //      /   \
        //     a     c
        val child2 = StaticParserRPQTest.Companion.create1LevelParseTree(
                ParseTree.CONCATENATION, Arrays.asList("a", "c"))
        val root2 = StaticParserRPQTest.Companion.create1LevelParseTree(
                ParseTree.CONCATENATION, Arrays.asList("d"))
        root2.children!!.add(0, child2)

        assertEquals(listOf(root1, root2), actual)
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
        val child2 = StaticParserRPQTest.create1LevelParseTree(
                ParseTree.UNION, Arrays.asList("b", "c"))
        val child1 = StaticParserRPQTest.create1LevelParseTree(
                ParseTree.CONCATENATION, Arrays.asList("a"))
        child1.setChild(1, child2)

        val root = StaticParserRPQTest.create1LevelParseTree(
                ParseTree.CONCATENATION, Arrays.asList("d"))
        root.children!!.add(0, child1)

        return root
    }
}
