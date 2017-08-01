/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.evaluationengine

import com.github.giedomak.telepathdb.datamodels.ParseTree
import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.planner.Planner
import com.github.giedomak.telepathdb.staticparser.StaticParserRPQTest
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
        val input = StaticParserRPQTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))

        // Our physical-plan:
        //        LOOKUP
        //         /  \
        //        a    b
        val physicalPlan = Planner.generate(input)

        // Gather the actual results from our EvaluationEngine.
        val actual = EvaluationEngine(mock).evaluate(physicalPlan).toList()

        assertEquals(expected, actual)
    }
}
