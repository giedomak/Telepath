package com.github.giedomak.telepath.physicaloperators

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.datamodels.PathTest
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.Edge
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.memorymanager.SimpleMemoryManager
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class SortMergeJoinTest {

    @Test
    fun evaluates() {

        val edgeSet = listOf(Edge("yay"), Edge("nooo"))
        val id = PathIdentifierStore.getPathIdByEdges(edgeSet)

        // Mocked results
        val paths1 = listOf(
                Path(id, PathTest.increasingNodes(3, 42)),
                Path(id, PathTest.increasingNodes(3, 49))
        )
        val paths2 = listOf(
                Path(id, PathTest.increasingNodes(3, 44)),
                Path(id, PathTest.increasingNodes(3, 48))
        )

        // Mock the evaluate call
        val operator1 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths1.map { it.inverse() }.stream(), false))
        }
        val operator2 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths2.stream(), false))
        }

        // Mock the actual operators
        val child1 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator1)
        }
        val child2 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator2)
        }

        // Expected result path
        val expected = Path(48, PathTest.increasingNodes(5, 42))

        // Mock the concatenation of paths into the expected
        val store = mock<PathIdentifierStore> {
            on { concatenatePaths(paths1.first(), paths2.first()) }.doReturn(expected)
        }

        // Mock the path identifier store to our module
        val telepath = mock<Telepath> {
            on { pathIdentifierStore }.doReturn(store)
            on { memoryManager }.doReturn(SimpleMemoryManager)
        }

        // Make sure our query knows about the module
        val query = mock<Query>()
        whenever(query.telepath).thenReturn(telepath)

        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf(), query)
        physicalPlan.children.addAll(listOf(child1, child2))

        println("Creating stream")

        val stream = SortMergeJoin(physicalPlan).evaluate()

        println("Terminating stream")

        val actual = stream.paths.toList()

        assertEquals(listOf(expected), actual)
    }
}
