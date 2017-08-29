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

        // Our expected results
        val paths = listOf(
                Path(pathId, PathTest.increasingNodes(3, 42)),
                Path(pathId, PathTest.increasingNodes(3, 49))
        )

        // Mock the path identifier store to return the expected pathId.
        val store = mock<PathIdentifierStore> {
            on { getPathIdByEdges(listOf(Edge("a"), Edge("b"))) }.doReturn(pathId)
        }

        // Mock the path index to return the expected results.
        val index = mock<KPathIndexInMemory> {
            on { search(PathPrefix(42)) }.doReturn(paths.stream())
        }

        // Mock our module to return the store and the index.
        val telepathdb = mock<TelepathDB> {
            on { kPathIndex }.doReturn(index)
            on { pathIdentifierStore }.doReturn(store)
        }

        // Let our query know the module we are using.
        val query = mock<Query> {
            on { telepathDB }.doReturn(telepathdb)
        }

        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), query)

        assertEquals(paths, IndexLookup(physicalPlan).evaluate().paths.toList())
    }
}