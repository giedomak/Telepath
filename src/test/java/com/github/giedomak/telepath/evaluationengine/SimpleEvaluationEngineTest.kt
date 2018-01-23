/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.evaluationengine

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.cardinalityestimation.KPathIndexCardinalityEstimation
import com.github.giedomak.telepath.datamodels.PathTest
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathPrefix
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepath.memorymanager.SimpleMemoryManager
import com.github.giedomak.telepath.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class SimpleEvaluationEngineTest {

    @Test
    fun evaluateLookup() {

        // Make the path along edge labels `a - b` known to the PathIdentifierStore.
        val id = PathIdentifierStore.getPathIdByEdgeLabel(listOf("a", "b"))

        // Our expected results, i.e. the Paths along the edge labels `a - b`.
        val expected = listOf(
                Path(id, PathTest.equalNodes(3, "42")),
                Path(id, PathTest.equalNodes(3, "33"))
        )

        // Mock our KPathIndexInMemory in order to return the expected results.
        val kPathIndexMock = mock<KPathIndexInMemory> {
            on { search(PathPrefix(id)) }.doReturn(expected.stream())
        }

        val telepathMock = mock<Telepath> {
            on { evaluationEngine }.doReturn(SimpleEvaluationEngine)
            on { kPathIndex }.doReturn(kPathIndexMock)
            on { pathIdentifierStore }.doReturn(PathIdentifierStore)
        }

        val queryMock = mock<Query> {
            on { telepath }.doReturn(telepathMock)
        }

        // Our physical-plan:
        //      INDEX_LOOKUP
        //         /  \
        //        a    b
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), queryMock)

        // Gather the actual results from our SimpleEvaluationEngine.
        val actual = SimpleEvaluationEngine.evaluate(physicalPlan).paths.toList()

        assertEquals(expected, actual)
    }

    @Test
    fun evaluateLookupWithIntermediateResults() {

        // Make the path along edge labels `a - b` known to the PathIdentifierStore.
        val id1 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("a", "b"))
        val id2 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("c", "d", "e"))
        val id3 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("a", "b", "c", "d", "e"))

        // Our expected results, i.e. the Paths along the edge labels `a - b`.
        val expected1 = listOf(
                Path(id1, PathTest.increasingNodes(3, 40)),
                Path(id1, PathTest.increasingNodes(3, 42))
        )
        val expected2 = listOf(
                Path(id2, PathTest.increasingNodes(4, 44)),
                Path(id2, PathTest.increasingNodes(4, 46))
        )

        // Mock our KPathIndexInMemory in order to return the expected results.
        val kPathIndexMock = mock<KPathIndexInMemory> {
            on { search(PathPrefix(id1)) }.doReturn(expected1.stream())
            on { search(PathPrefix(id2)) }.doReturn(expected2.stream())
        }

        val cardinalityEstimationMock = mock<KPathIndexCardinalityEstimation> {
            on { getCardinality(any<PhysicalPlan>()) }.doReturn(20)
        }

        val telepathMock = mock<Telepath> {
            on { evaluationEngine }.doReturn(SimpleEvaluationEngine)
            on { kPathIndex }.doReturn(kPathIndexMock)
            on { pathIdentifierStore }.doReturn(PathIdentifierStore)
            on { memoryManager }.doReturn(SimpleMemoryManager)
            on { cardinalityEstimation }.doReturn(cardinalityEstimationMock)
        }

        val queryMock = mock<Query>()
        whenever(queryMock.telepath).doReturn(telepathMock)

        // Our physical-plan:
        //         HASH_JOIN
        //          /    \
        //  INDEX_LOOKUP INDEX_LOOKUP
        //      / \      / | \
        //     a   b    c  d  e
        val child1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), queryMock)
        val child2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("c", "d", "e"), queryMock)
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.HASH_JOIN, listOf(), queryMock)
        physicalPlan.children.addAll(listOf(child1, child2))

        // Gather the actual results from our SimpleEvaluationEngine.
        val actual = SimpleEvaluationEngine.evaluate(physicalPlan).paths.toList()

        assertEquals(listOf(Path(id3, PathTest.increasingNodes(6, 42))), actual)
    }
}
