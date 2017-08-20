/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.staticparser

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlanTest
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*

class StaticParserRPQTest {

    @Test
    fun inputBecomesAParseTree() {
        // Parse the input into a LogicalPlan
        val actual = StaticParserRPQ.parse(Query(mock(), "a/b"))

        // Then
        assertEquals(LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b")), actual)
    }

    @Test
    fun concatenationInput() {
        // Parse the input into a LogicalPlan
        val actual = StaticParserRPQ.parse(Query(mock(), "a/b"))

        // Create the expected LogicalPlan
        val a = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, Arrays.asList("a", "b"))
        val b = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, Arrays.asList("a", "c"))
        val c = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, Arrays.asList("b", "b"))
        val d = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, Arrays.asList("a", "b", "c"))

        // Then
        assertEquals(a, actual)
        assertNotEquals(b, actual)
        assertNotEquals(c, actual)
        assertNotEquals(d, actual)
    }

    @Test
    fun unionInput() {
        // Parse the input into a LogicalPlan
        val actual = StaticParserRPQ.parse(Query(mock(), "a|b"))

        // Create the expected LogicalPlan
        val a = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, Arrays.asList("a", "b"))
        val b = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, Arrays.asList("a", "c"))
        val c = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, Arrays.asList("b", "b"))
        val d = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, Arrays.asList("a", "b", "c"))

        // Then
        assertEquals(a, actual)
        assertNotEquals(b, actual)
        assertNotEquals(c, actual)
        assertNotEquals(d, actual)
    }
}
