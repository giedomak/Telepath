/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.cardinalityestimation.KPathIndexCardinalityEstimation
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlanTest
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Test

class DynamicProgrammingPlannerTest {

    // MOCKS

    val cardinalityEstimationMock = mock<KPathIndexCardinalityEstimation> {
        on { getCardinality(any<PhysicalPlan>()) }.doReturn(20)
    }

    val telepathDBMock = mock<TelepathDB> {
        on { cardinalityEstimation }.doReturn(cardinalityEstimationMock)
        on { planner }.doReturn(DynamicProgrammingPlanner)
    }

    val queryMock = mock<Query> {
        on { telepathDB }.doReturn(telepathDBMock)
    }

    @Test
    fun generatesSimplePhysicalPlan() {

        // Generate the LogicalPlan for input
        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"), queryMock)

        // Generate the physical plan
        val actual = telepathDBMock.planner.generate(input)

        // Generate the expected physical plan
        val expected = PhysicalPlanTest.generatePhysicalPlan(PhysicalPlan.INDEXLOOKUP, listOf("a", "b"))

        assertEquals(expected, actual)
    }

    @Test
    fun generatesMultiLevelPhysicalPlan() {

        // Input:
        //       CONCATENATION
        //        /      \
        //       a   CONCATENATION
        //              /   \
        //             b     c
        val child = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("b", "c"), queryMock)
        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a"), queryMock)
        input.setChild(1, child)

        // Parse the input
        val actual = telepathDBMock.planner.generate(input)

        // Generate the expected physical plan
        //      INDEXLOOKUP
        //        /  |  \
        //       a   b   c
        val expected = PhysicalPlanTest.generatePhysicalPlan(PhysicalPlan.INDEXLOOKUP, listOf("a", "b", "c"))

        assertEquals(expected, actual)
    }
}
