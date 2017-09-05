package com.github.giedomak.telepathdb.cardinalityestimation.synopsis

import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import org.junit.Test
import kotlin.test.assertEquals

class SynopsisTest {

    @Test
    fun tracksStatistics() {

        createSynopsis()

        assertEquals(2, Synopsis.out(Edge("a")))
        assertEquals(2, Synopsis.`in`(Edge("a")))
        assertEquals(3, Synopsis.paths(Edge("a")))
        assertEquals(2, Synopsis.pairs(Edge("a")))

        assertEquals(2, Synopsis.out(Edge("b")))
        assertEquals(1, Synopsis.`in`(Edge("b")))
        assertEquals(2, Synopsis.paths(Edge("b")))
        assertEquals(2, Synopsis.pairs(Edge("b")))

        assertEquals(1, Synopsis.out(Edge("c")))
        assertEquals(1, Synopsis.`in`(Edge("c")))
        assertEquals(1, Synopsis.paths(Edge("c")))
        assertEquals(1, Synopsis.pairs(Edge("c")))

        assertEquals(4, Synopsis.out(Pair(Edge("a"), Edge("b"))))
        assertEquals(4, Synopsis.`in`(Pair(Edge("a"), Edge("b"))))
        assertEquals(4, Synopsis.middle(Pair(Edge("a"), Edge("b"))))
        assertEquals(5, Synopsis.paths(Pair(Edge("a"), Edge("b"))))
        assertEquals(4, Synopsis.pairs(Pair(Edge("a"), Edge("b"))))
        assertEquals(2, Synopsis.one(Pair(Edge("a"), Edge("b"))))
        assertEquals(2, Synopsis.two(Pair(Edge("a"), Edge("b"))))

        assertEquals(3, Synopsis.out(Pair(Edge("b"), Edge("c"))))
        assertEquals(3, Synopsis.`in`(Pair(Edge("b"), Edge("c"))))
        assertEquals(3, Synopsis.middle(Pair(Edge("b"), Edge("c"))))
        assertEquals(4, Synopsis.paths(Pair(Edge("b"), Edge("c"))))
        assertEquals(3, Synopsis.pairs(Pair(Edge("b"), Edge("c"))))
        assertEquals(1, Synopsis.one(Pair(Edge("b"), Edge("c"))))
        assertEquals(1, Synopsis.two(Pair(Edge("b"), Edge("c"))))

    }

    private fun createSynopsis() {

        val id1 = PathIdentifierStore.getPathIdByEdgeLabel("a")
        val id2 = PathIdentifierStore.getPathIdByEdgeLabel("b")
        val id3 = PathIdentifierStore.getPathIdByEdgeLabel("c")

        val id4 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("a", "b"))
        val id5 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("b", "c"))

        val paths = listOf(
                Path(id1, PathTest.increasingNodes(2, 42)),
                Path(id1, PathTest.increasingNodes(2, 42)),
                Path(id1, PathTest.increasingNodes(2, 44)),
                Path(id2, PathTest.increasingNodes(2, 45)),
                Path(id2, PathTest.equalNodes(2, 46)),
                Path(id3, PathTest.increasingNodes(2, 46))
        )

        val paths2 = listOf(
                Path(id4, PathTest.increasingNodes(3, 42)),
                Path(id4, PathTest.increasingNodes(3, 42)),
                Path(id4, PathTest.increasingNodes(3, 43)),
                Path(id4, PathTest.increasingNodes(3, 44)),
                Path(id4, PathTest.increasingNodes(3, 45)),
                Path(id5, PathTest.increasingNodes(3, 42)),
                Path(id5, PathTest.increasingNodes(3, 43)),
                Path(id5, PathTest.increasingNodes(3, 43)),
                Path(id5, PathTest.increasingNodes(3, 45))
        )

        (paths + paths2).forEach { Synopsis.handleInsertion(it) }

        Synopsis.afterMath()
    }

}