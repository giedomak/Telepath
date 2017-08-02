/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.evaluationengine

import com.github.giedomak.telepathdb.datamodels.*
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.planner.Planner
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class EvaluationEngineTest {

    @Test
    fun evaluateLookup() {

        // Make the path along edge labels `a - b` known to the PathIdentifierStore.
        val id = PathIdentifierStore.getPathIdByEdgeLabel(listOf("a", "b"))

        // Our expected results, i.e. the Paths along the edge labels `a - b`.
        val expected = listOf(
                Path(id, PathTest.equalNodes(3, 42)),
                Path(id, PathTest.equalNodes(3, 33))
        )

        // Mock our KPathIndexInMemory in order to return the expected results.
        val mock = mock<KPathIndexInMemory> {
            on { search(PathPrefix(id)) }.doReturn(expected.stream())
        }

        // The input:
        //       CONCATENATION
        //          /   \
        //         a     b
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))

        // Our physical-plan:
        //        LOOKUP
        //         /  \
        //        a    b
        val physicalPlan = Planner.generate(input)

        // Gather the actual results from our EvaluationEngine.
        val actual = EvaluationEngine(mock).evaluate(physicalPlan).toList()

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
        val mock = mock<KPathIndexInMemory> {
            on { search(PathPrefix(id1)) }.doReturn(expected1.stream())
            on { search(PathPrefix(id2)) }.doReturn(expected2.stream())
        }

        // The input:
        //       CONCATENATION
        //        / | | | \
        //       a  b c d  e
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b", "c", "d", "e"))

        // Our physical-plan:
        //     CONCATENATION
        //        /    \
        //    LOOKUP  LOOKUP
        //     / \    / | \
        //    a   b  c  d  e
        val physicalPlan = Planner.generate(input)

        // Gather the actual results from our EvaluationEngine.
        val actual = EvaluationEngine(mock).evaluate(physicalPlan).toList()

        assertEquals(listOf(Path(id3, PathTest.increasingNodes(6, 42))), actual)
    }
}
