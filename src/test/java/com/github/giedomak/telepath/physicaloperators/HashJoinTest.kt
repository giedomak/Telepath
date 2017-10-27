package com.github.giedomak.telepath.physicaloperators

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.datamodels.PathTest
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.memorymanager.SimpleMemoryManager
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class HashJoinTest {

    @Test
    fun evaluates() {

        // Mocked results
        val paths1 = listOf(
                Path(42, PathTest.increasingNodes(3, 42)),
                Path(42, PathTest.increasingNodes(3, 49))
        )
        val paths2 = listOf(
                Path(42, PathTest.increasingNodes(3, 44)),
                Path(42, PathTest.increasingNodes(3, 48))
        )

        // Mock the evaluate call
        val operator1 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths1.stream(), false))
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
        val telepathMock = mock<Telepath> {
            on { pathIdentifierStore }.doReturn(store)
            on { memoryManager }.doReturn(SimpleMemoryManager)
        }

        // Make sure our query knows about the module
        val query = mock<Query> {
            on { telepath }.doReturn(telepathMock)
        }

        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf(), query)
        physicalPlan.children.addAll(listOf(child1, child2))

        assertEquals(listOf(expected), HashJoin(physicalPlan).evaluate().paths.toList())
    }
}