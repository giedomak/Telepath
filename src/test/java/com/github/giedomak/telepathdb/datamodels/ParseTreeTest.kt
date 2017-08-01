package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.staticparser.StaticParserRPQTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

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
    fun outputsToString() {
        val actual = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, Arrays.asList("a", "b"))
        val expected = "ParseTree(id=1, operator=CONCATENATION, leaf=null, edge=null, isRoot=true, children=" + actual.children.toString() + ")"

        // then
        assertEquals(expected, actual.toString())
    }

}
