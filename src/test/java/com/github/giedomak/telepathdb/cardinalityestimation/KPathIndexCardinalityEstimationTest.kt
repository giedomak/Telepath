/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertEquals

class KPathIndexCardinalityEstimationTest {

    @Test
    fun returnsTheCardinalityOfPathsIds() {

        val cardinalityEstimation = KPathIndexCardinalityEstimation(TelepathDB.kPathIndex)

        createIndex()

        cardinalityEstimation.getCardinality(1)
        assertEquals(10, cardinalityEstimation.getCardinality(1))
        assertEquals(9, cardinalityEstimation.getCardinality(12))
        assertEquals(8, cardinalityEstimation.getCardinality(24))
        assertEquals(7, cardinalityEstimation.getCardinality(36))
        assertEquals(6, cardinalityEstimation.getCardinality(42))
        assertEquals(5, cardinalityEstimation.getCardinality(56))
        assertEquals(4, cardinalityEstimation.getCardinality(68))
        assertEquals(3, cardinalityEstimation.getCardinality(71))
        assertEquals(2, cardinalityEstimation.getCardinality(90))
        assertEquals(1, cardinalityEstimation.getCardinality(99))
        assertEquals(1, cardinalityEstimation.getCardinality(1039), "returns 1 on non-existing pathId")
    }

    @Test
    fun returnsTheCardinalityOfUnion() {

        val cardinalityEstimation = spy(KPathIndexCardinalityEstimation(TelepathDB.kPathIndex))
        doReturn(10L).whenever(cardinalityEstimation).getCardinality(any<Long>())

        val child1 = spy(PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a")))
        doReturn(1L).whenever(child1).pathIdOfChildren()

        val child2 = spy(PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("b")))
        doReturn(2L).whenever(child2).pathIdOfChildren()

        val physicalPlan = PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.UNION, listOf(child1, child2))

        val actual = cardinalityEstimation.getCardinality(physicalPlan)

        assertEquals(20L, actual)
    }

    @Test
    fun returnsTheCardinalityOfJoins() {

        val cardinalityEstimation = spy(KPathIndexCardinalityEstimation(TelepathDB.kPathIndex))
        doReturn(10L).whenever(cardinalityEstimation).getCardinality(any<Long>())

        val child1 = spy(PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a")))
        doReturn(1L).whenever(child1).pathIdOfChildren()

        val child2 = spy(PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("b")))
        doReturn(2L).whenever(child2).pathIdOfChildren()

        PhysicalOperator.JOIN_OPERATORS.forEach {

            val physicalPlan = PhysicalPlanTest.generatePhysicalPlanWithChildren(it, listOf(child1, child2))

            val actual = cardinalityEstimation.getCardinality(physicalPlan)

            assertEquals(10L, actual)
        }
    }

    private fun createIndex() {
        for (_i in 1..10L) {
            for (i in 1..(10 * _i)) {
                TelepathDB.kPathIndex.insert(Path(i, PathTest.equalNodes(3, 42)))
            }
        }
    }
}
