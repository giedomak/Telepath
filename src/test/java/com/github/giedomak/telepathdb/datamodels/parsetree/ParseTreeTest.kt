package com.github.giedomak.telepathdb.datamodels.parsetree

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
