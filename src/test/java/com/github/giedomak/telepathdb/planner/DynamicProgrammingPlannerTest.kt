/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.cardinalityestimation.KPathIndexCardinalityEstimation
import com.github.giedomak.telepathdb.costmodel.AdvancedCostModel
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlanTest
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.github.giedomak.telepathdb.planner.enumerator.SimpleEnumerator
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Test

class DynamicProgrammingPlannerTest {

    // MOCKS

    private val cardinalityEstimationMock = mock<KPathIndexCardinalityEstimation> {
        on { getCardinality(any<PhysicalPlan>()) }.doReturn(20)
    }

    private val indexMock = mock<KPathIndexInMemory> {
        on { k }.doReturn(3)
    }

    private val telepathDBMock = mock<TelepathDB> {
        on { cardinalityEstimation }.doReturn(cardinalityEstimationMock)
        on { planner }.doReturn(DynamicProgrammingPlanner)
        on { costModel }.doReturn(AdvancedCostModel)
        on { pathIdentifierStore }.doReturn(PathIdentifierStore)
        on { enumerator }.doReturn(SimpleEnumerator)
        on { kPathIndex }.doReturn(indexMock)
    }

    private val queryMock = mock<Query> {
        on { telepathDB }.doReturn(telepathDBMock)
    }

    @Test
    fun generatesSimplePhysicalPlan() {

        // Generate the LogicalPlan for input
        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"), queryMock)

        // Generate the physical plan
        val actual = telepathDBMock.planner.generate(input)

        // Generate the expected physical plan
        val expected = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"))

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
        //      INDEX_LOOKUP
        //        /  |  \
        //       a   b   c
        val expected = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b", "c"))

        assertEquals(expected, actual)
    }
}
