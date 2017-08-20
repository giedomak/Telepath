package com.github.giedomak.telepathdb.datamodels.parsetree

import com.github.giedomak.telepathdb.datamodels.Edge
import com.github.giedomak.telepathdb.datamodels.Query
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.stream.IntStream
import kotlin.test.assertNotEquals

class ParseTreeTest {

    @Test
    fun postOrderTreeWalk() {
        // given
        // Root has id 1, its three children will have ids 2, 3 and 4.
        val root = ParseTree(mock())
        root.setLeaf("1")
        IntStream.range(2, 5).forEach { root.children.add(ParseTree(mock(), ParseTree.LEAF, Edge(it.toString()))) }

        // The middle child will get two more childs with ids 5 and 6
        IntStream.range(5, 7).forEach { root.getChild(1)!!.children.add(ParseTree(mock(), ParseTree.LEAF, Edge(it.toString()))) }

        //          1
        //        / | \
        //       2  3  4
        //         / \
        //        5   6

        // We expect the post-order tree-walk to report 2, 5, 6, 3, 4, 1
        val expected = listOf(2, 5, 6, 3, 4, 1).toString()
        val actual = root.postOrderTraversal().map { it.leaf!!.label }.toList().toString()

        // then
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
        val actual = create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))
        val expected = "ParseTree(operator=CONCATENATION, leaf=null, children=" + actual.children.toString() + ")"

        // then
        assertEquals(expected, actual.toString())
    }

    //
    // ----------- COMPANION  -----------
    //

    companion object {

        // ParseTree without children, just a leaf.
        fun createSimpleParseTree(label: String): ParseTree {
            val parseTree = ParseTree(mock())
            parseTree.setLeaf(label)
            return parseTree
        }

        // ParseTree with 1 level of children, root will get the operator param.
        fun create1LevelParseTree(operator: Int, labels: List<String>, query: Query = mock()): ParseTree {
            val parseTree = ParseTree(query)
            parseTree.operator = operator

            // Create the children and add them to the root
            for (i in labels.indices) {
                val child = ParseTree(mock())
                child.setLeaf(labels[i])
                parseTree.setChild(i, child)
            }

            return parseTree
        }
    }
}
