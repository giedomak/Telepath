package com.github.giedomak.telepathdb.cardinalityestimation.synopsis

import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import org.junit.Test
import kotlin.test.assertEquals

class SynopsisTest {

    private val synopsis = Synopsis()

    init {
        createSynopsis()
    }

    @Test
    fun tracksSyn1Statistics() {

        assertEquals(2, synopsis.out(Edge("a")))
        assertEquals(2, synopsis.`in`(Edge("a")))
        assertEquals(3, synopsis.paths(Edge("a")))
        assertEquals(2, synopsis.pairs(Edge("a")))

        assertEquals(2, synopsis.out(Edge("b")))
        assertEquals(1, synopsis.`in`(Edge("b")))
        assertEquals(2, synopsis.paths(Edge("b")))
        assertEquals(2, synopsis.pairs(Edge("b")))

        assertEquals(1, synopsis.out(Edge("c")))
        assertEquals(1, synopsis.`in`(Edge("c")))
        assertEquals(1, synopsis.paths(Edge("c")))
        assertEquals(1, synopsis.pairs(Edge("c")))

    }

    @Test
    fun tracksSyn2Statistics() {

        assertEquals(4, synopsis.out(Pair(Edge("a"), Edge("b"))))
        assertEquals(4, synopsis.`in`(Pair(Edge("a"), Edge("b"))))
        assertEquals(4, synopsis.middle(Pair(Edge("a"), Edge("b"))))
        assertEquals(5, synopsis.paths(Pair(Edge("a"), Edge("b"))))
        assertEquals(4, synopsis.pairs(Pair(Edge("a"), Edge("b"))))
        assertEquals(2, synopsis.one(Pair(Edge("a"), Edge("b"))))
        assertEquals(2, synopsis.two(Pair(Edge("a"), Edge("b"))))

        assertEquals(3, synopsis.out(Pair(Edge("b"), Edge("c"))))
        assertEquals(3, synopsis.`in`(Pair(Edge("b"), Edge("c"))))
        assertEquals(3, synopsis.middle(Pair(Edge("b"), Edge("c"))))
        assertEquals(4, synopsis.paths(Pair(Edge("b"), Edge("c"))))
        assertEquals(3, synopsis.pairs(Pair(Edge("b"), Edge("c"))))
        assertEquals(1, synopsis.one(Pair(Edge("b"), Edge("c"))))
        assertEquals(1, synopsis.two(Pair(Edge("b"), Edge("c"))))

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

        (paths + paths2).forEach { synopsis.handleInsertion(it) }

        synopsis.afterMath()
    }

}