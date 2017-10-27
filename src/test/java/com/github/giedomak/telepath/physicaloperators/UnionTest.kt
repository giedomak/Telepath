package com.github.giedomak.telepath.physicaloperators

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.datamodels.PathTest
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepath.memorymanager.SimpleMemoryManager
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class UnionTest {

    @Test
    fun evaluates() {

        // Mock the evaluation of its two children
        val paths1 = listOf(
                Path(42, PathTest.increasingNodes(3, 42)),
                Path(42, PathTest.increasingNodes(3, 49))
        )
        val paths2 = listOf(
                Path(42, PathTest.increasingNodes(3, 44)),
                Path(42, PathTest.increasingNodes(3, 48))
        )

        // Mock them into the evaluation call
        val operator1 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths1.stream(), false))
        }
        val operator2 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths2.stream(), false))
        }

        // Mock them into the operator
        val child1 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator1)
        }
        val child2 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator2)
        }

        // Mock the path identifier store to our module
        val telepathMock = mock<Telepath> {
            on { memoryManager }.doReturn(SimpleMemoryManager)
        }

        // Make sure our query knows about the module
        val query = mock<Query> {
            on { telepath }.doReturn(telepathMock)
        }

        // Input with the mocked children
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.UNION, listOf(), query)
        physicalPlan.children.addAll(listOf(child1, child2))

        val expected = listOf(paths1, paths2).flatten()

        assertEquals(expected, Union(physicalPlan).evaluate().paths.toList())
    }

    @Test
    fun evaluatesDistinct() {

        // Mock the evaluation of its two children
        val paths1 = listOf(
                Path(42, PathTest.increasingNodes(3, 42)),
                Path(42, PathTest.increasingNodes(3, 49))
        )
        // This one now only holds a duplicate, so should be omitted in the results.
        val paths2 = listOf(
                Path(42, PathTest.increasingNodes(3, 49))
        )

        // Mock them into the evaluation call
        val operator1 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths1.stream(), false))
        }
        val operator2 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(null, paths2.stream(), false))
        }

        // Mock them into the operator
        val child1 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator1)
        }
        val child2 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator2)
        }

        // Mock the path identifier store to our module
        val telepathMock = mock<Telepath> {
            on { memoryManager }.doReturn(SimpleMemoryManager)
        }

        // Make sure our query knows about the module
        val query = mock<Query> {
            on { telepath }.doReturn(telepathMock)
        }

        // Input with the mocked children
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.UNION, listOf(), query)
        physicalPlan.children.addAll(listOf(child1, child2))

        assertEquals(paths1, Union(physicalPlan).evaluate().paths.toList())
    }
}