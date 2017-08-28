package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class IndexLookupTest {

    @Test
    fun evaluates() {

        val pathId = 42L

        val paths = listOf(
                Path(pathId, PathTest.increasingNodes(3, 42)),
                Path(pathId, PathTest.increasingNodes(3, 49))
        )

        val store = mock<PathIdentifierStore> {
            on { getPathIdByEdges(listOf(Edge("a"), Edge("b"))) }.doReturn(pathId)
        }

        val index = mock<KPathIndexInMemory> {
            on { search(PathPrefix(42)) }.doReturn(paths.stream())
        }

        val telepathdb = mock<TelepathDB> {
            on { kPathIndex }.doReturn(index)
            on { pathIdentifierStore }.doReturn(store)
        }

        val query = mock<Query> {
            on { telepathDB }.doReturn(telepathdb)
        }

        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), query)

        assertEquals(paths, IndexLookup(physicalPlan).evaluate().paths.toList())
    }
}