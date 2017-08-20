/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import com.github.giedomak.telepathdb.datamodels.PathTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CardinalityEstimationTest {

    @Test
    fun returnsTheCardinality() {
        assertEquals(10, PathPrefix(1).cardinality)
        assertEquals(9, PathPrefix(12).cardinality)
        assertEquals(8, PathPrefix(24).cardinality)
        assertEquals(7, PathPrefix(36).cardinality)
        assertEquals(6, PathPrefix(42).cardinality)
        assertEquals(5, PathPrefix(56).cardinality)
        assertEquals(4, PathPrefix(68).cardinality)
        assertEquals(3, PathPrefix(71).cardinality)
        assertEquals(2, PathPrefix(90).cardinality)
        assertEquals(1, PathPrefix(99).cardinality)
        assertEquals(1, PathPrefix(1039).cardinality, "returns 0 on non-existing pathId")
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
