/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.PathTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class KPathIndexCardinalityEstimationTest {

    private val cardinalityEstimation = KPathIndexCardinalityEstimation(TelepathDB.kPathIndex)

    @Test
    fun returnsTheCardinality() {
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

    @Before
    fun createIndex() {
        for (_i in 1..10L) {
            for (i in 1..(10 * _i)) {
                TelepathDB.kPathIndex.insert(Path(i, PathTest.equalNodes(3, 42)))
            }
        }
    }
}
