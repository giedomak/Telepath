package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.test.assertNotEquals

class ParseTreeTest {

    @Test
    fun postOrderTreeWalk() {
        // given
        // Root has id 1, its three children will have ids 2, 3 and 4.
        val root = ParseTree(true)
        IntStream.range(0, 3).forEach { root.children.add(ParseTree()) }

        // The middle child will get two more childs with ids 5 and 6
        IntStream.range(0, 2).forEach { root.getChild(1)!!.children.add(ParseTree()) }

        //          1
        //        / | \
        //       2  3  4
        //         / \
        //        5   6

        // We expect the post-order tree-walk to report 2, 5, 6, 3, 4, 1
        val offset = root.id - 1 // since these ids are auto-generated, we might have an offset in this test.
        val expected = listOf(2, 5, 6, 3, 4, 1).map { it + offset }.toString()
        val actual = root.postOrderTreeWalk().map { it.id }.collect(Collectors.toList()).toString()

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize2() {
        // Input:
        //       CONCATENATION
        //        /   |   \
        //       a    b  UNION
        //                / \
        //               c   d
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"), false)
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))
        input.children.add(child)

        // Expected:
        //   CONCATENATION   UNION
        //       /  \         / \
        //      a    b       c   d
        val expected = listOf(
                ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"), true),
                ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"), false)
        )

        val actual = input.subtreesOfSize(2)

        assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize2Complex() {
        // Input:
        //         CONCATENATION
        //        /  |     |  |  \
        //       a  UNION  e  f   g
        //          / | \
        //         b  c  d
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("b", "c", "d"), false)
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "e", "f", "g"))
        input.children.add(1, child)

        // Expected:
        //     UNION   UNION    CONCATENATION    CONCATENATION
        //      / \     / \         /   \            /   \
        //     b   c   c   d       e     f          f     g
        val expected = listOf(
                ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("b", "c"), false),
                ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"), false),
                ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("e", "f"), true),
                ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("f", "g"), true)
        )

        val actual = input.subtreesOfSize(2)

        assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize3() {
        // Input:
        //        CONCATENATION
        //        /  |    |   \
        //       a   b  UNION  d
        //               / \
        //              c   d
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.UNION, listOf("c", "d"), false)
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "d"))
        input.children.add(2, child)

        // Expected:
        //      CONCATENATION    CONCATENATION
        //         /   \             /   \
        //        b   UNION       UNION   d
        //             / \         / \
        //            c   d       c   d
        val expected1 = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("b"), true)
        expected1.children.add(child)
        val expected2 = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("d"), true)
        expected2.children.add(0, child)
        val expected = listOf(expected1, expected2)

        val actual = input.subtreesOfSize(3)

        assertEquals(expected, actual)
    }

    @Test
    fun equalsParseTrees() {
        val a = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))
        val b = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))
        val c = create1LevelParseTree(ParseTree.UNION, listOf("a", "b"))
        val d = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "c"))
        val e = create1LevelParseTree(ParseTree.UNION, listOf("a", "b", "c"))

        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun hashCodeParseTrees() {
        val a = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b")).hashCode()
        val b = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b")).hashCode()
        val c = create1LevelParseTree(ParseTree.UNION, listOf("a", "b")).hashCode()
        val d = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "c")).hashCode()
        val e = create1LevelParseTree(ParseTree.UNION, listOf("a", "b", "c")).hashCode()

        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(c, e)
        assertNotEquals(d, e)
    }

    @Test
    fun outputsToString() {
        val actual = create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "b"))
        val expected = "ParseTree(id=" + actual.id + ", operator=CONCATENATION, leaf=null, isRoot=true, children=" + actual.children.toString() + ")"

        // then
        assertEquals(expected, actual.toString())
    }

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // ParseTree without children, just a leaf.
        fun createSimpleParseTree(label: String, isRoot: Boolean = true): ParseTree {
            val parseTree = ParseTree(isRoot)
            parseTree.setLeaf(label)
            return parseTree
        }

        // ParseTree with 1 level of children, root will get the operator param.
        @JvmOverloads fun create1LevelParseTree(operator: Int, labels: List<String>, isRoot: Boolean = true): ParseTree {
            val parseTree = ParseTree(isRoot)
            parseTree.operatorId = operator

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
