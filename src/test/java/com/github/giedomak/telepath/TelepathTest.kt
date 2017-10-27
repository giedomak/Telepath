/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath

import com.github.giedomak.telepath.cardinalityestimation.CardinalityEstimation
import com.github.giedomak.telepath.datamodels.PathTest
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.LogicalPlanTest
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.evaluationengine.SimpleEvaluationEngine
import com.github.giedomak.telepath.physicaloperators.PhysicalOperator
import com.github.giedomak.telepath.planner.Planner
import com.github.giedomak.telepath.staticparser.StaticParserRPQ
import com.nhaarman.mockito_kotlin.*
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TelepathTest {

    // Need to test console output (System.out)
    private val outContent = ByteArrayOutputStream()
    private val stdout = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
    }

    @After
    fun cleanUpStreams() {
        System.setOut(stdout)
    }

    @Test
    fun producesResults() {

        // Mock Telepath
        val telepath = spy<Telepath>()

        // Let's do an end-to-end test for:
        //        CONCATENATION
        //           /   \
        //          a     b
        val logicalPlan = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"), Query(telepath, ""))
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), Query(telepath, ""))
        // Catch the pathId of `a - b`
        val pathId = PathIdentifierStore.getPathIdByEdgeLabel(listOf("a", "b"))

        // Our expected results
        val expectedPaths = listOf(
                PathTest.simplePath(pathId, 3, "42"),
                PathTest.simplePath(pathId, 3, "45")
        )

        // Mocking all the modules we'll use
        val staticParser = mock<StaticParserRPQ> {
            on { parse(any()) }.doReturn(logicalPlan)
        }
        val pathStream = mock<PathStream> {
            on { paths }.doReturn(expectedPaths.stream())
        }
        val evaluationEngine = mock<SimpleEvaluationEngine> {
            on { evaluate(physicalPlan) }.doReturn(pathStream)
        }
        val cardinalityEstimationMock = mock<CardinalityEstimation> {
            on { getCardinality(any()) }.doReturn(20)
        }
        val plannerMock = mock<Planner> {
            on { generate(logicalPlan) }.doReturn(physicalPlan)
        }

        // Set all the mocks
        telepath.staticParser = staticParser
        telepath.evaluationEngine = evaluationEngine
        telepath.cardinalityEstimation = cardinalityEstimationMock
        telepath.planner = plannerMock
        // Don't wait for a second user input --> throw exception
        doReturn("a/b").doThrow(IllegalArgumentException()).whenever(telepath).getUserInput(any())

        // Since our second input will throw an exception, we'll catch it.
        try {
            telepath.start()
        } catch (e: IllegalArgumentException) {
        }

        // Assert that we have both paths printed in the results
        for (path in expectedPaths) {
            assertThat(outContent.toString(), containsString(path.toString()))
        }
    }
}
