package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class HashJoinTest {

    @Test
    fun evaluates() {

        val paths1 = listOf(
                Path(42, PathTest.increasingNodes(3, 42)),
                Path(42, PathTest.increasingNodes(3, 49))
        )

        val paths2 = listOf(
                Path(42, PathTest.increasingNodes(3, 44)),
                Path(42, PathTest.increasingNodes(3, 48))
        )

        val operator1 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(paths1.stream()))
        }

        val operator2 = mock<PhysicalOperator> {
            on { evaluate() }.doReturn(PathStream(paths2.stream()))
        }

        val child1 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator1)
        }

        val child2 = mock<PhysicalPlan> {
            on { physicalOperator }.doReturn(operator2)
        }

        val expected = Path(48, PathTest.increasingNodes(5, 42))

        val store = mock<PathIdentifierStore> {
            on { concatenatePaths(paths1.first(), paths2.first()) }.doReturn(expected)
        }

        val telepathdb = mock<TelepathDB> {
            on { pathIdentifierStore }.doReturn(store)
        }

        val query = mock<Query> {
            on { telepathDB }.doReturn(telepathdb)
        }

        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf(), query)
        physicalPlan.children.addAll(listOf(child1, child2))

        assertEquals(listOf(expected), HashJoin(physicalPlan).evaluate().paths.toList())
    }
}