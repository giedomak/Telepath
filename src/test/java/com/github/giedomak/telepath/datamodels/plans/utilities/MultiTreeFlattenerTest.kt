/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.datamodels.plans.utilities

import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.LogicalPlanTest
import org.junit.Test
import kotlin.test.assertEquals

class MultiTreeFlattenerTest {

    @Test
    fun flattensConcatenation() {

        // Given:
        //       CONCATENATION
        //          /    \
        //         a    CONCATENATION
        //                 /    \
        //                b      c
        val child = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("b", "c"))
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a"))
        root.setChild(1, child)

        // Expected:
        //       CONCATENATION
        //          /  |  \
        //         a   b   c
        val expected = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c"))

        // Then
        assertEquals(expected, root.flatten())
    }

    @Test
    fun flattensMultipleConcatenation() {

        // Given:
        //                 CONCATENATION
        //          /        /       |      \
        //         a  CONCATENATION  d  CONCATENATION
        //                 /    \         /   \
        //                b      c       e     f
        val child1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("b", "c"))
        val child2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("e", "f"))
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "d"))
        root.children.add(1, child1)
        root.children.add(3, child2)

        // Expected:
        //          CONCATENATION
        //          /  / | | | \
        //         a  b  c d e  f
        val expected = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c", "d", "e", "f"))

        // Then
        assertEquals(expected, root.flatten())
    }

    @Test
    fun flattensComplexConcatenation() {

        // Given:
        //       CONCATENATION
        //          /    \
        //         a    CONCATENATION
        //                 /      \
        //        CONCATENATION    d
        //           /    \
        //          b      c
        val child1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("b", "c"))
        val child2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("d"))
        child2.children.add(0, child1)
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a"))
        root.setChild(1, child2)

        // Expected:
        //        CONCATENATION
        //          /  |  |  \
        //         a   b  c   c
        val expected = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c", "d"))

        // Then
        assertEquals(expected, root.flatten())
    }

    @Test
    fun flattensComplexOperatorCombination() {

        // Given:
        //            CONCATENATION
        //          /       |        \
        //         a  CONCATENATION   f
        //              /      \
        //          UNION       e
        //         /    \
        //        b    UNION
        //             /  \
        //            c    d
        val child1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("c", "d"))
        val child2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("b"))
        child2.children.add(child1)
        val child3 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("e"))
        child3.children.add(0, child2)
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "f"))
        root.children.add(1, child3)

        // Expected:
        //        CONCATENATION
        //        /   |    |  \
        //       a  UNION  e   f
        //          / | \
        //         b  c  d
        val child = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("b", "c", "d"))
        val expected = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "e", "f"))
        expected.children.add(1, child)

        // Then
        assertEquals(expected, root.flatten())
    }
}
