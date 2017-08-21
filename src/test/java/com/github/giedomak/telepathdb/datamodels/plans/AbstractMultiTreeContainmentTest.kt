/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.plans;

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AbstractMultiTreeContainmentTest {

    @Test
    fun fullChildContainmentCheck() {

        // Given s1 & s2:
        //    CONCATENATION              CONCATENATION
        //       /    \                     /    \
        //      a      b                   c      d
        //
        // Root:
        //               UNION
        //               /   \
        //   CONCATENATION   CONCATENATION
        //       /   \          /     \
        //      a     b        c       d
        val s1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val s2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("c", "d"))
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, emptyList())
        root.children.addAll(listOf(s1, s2))

        assertTrue(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.UNION))
        assertFalse(root.containsSubtreesThroughOperator(s2, s1, LogicalPlan.UNION))
        assertFalse(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.CONCATENATION))
    }

    @Test
    fun middleChildContainmentCheck() {

        // Given s1 & s2:
        //    CONCATENATION              CONCATENATION
        //       /    \                     /    \
        //      a      b                   d      e
        //
        // Root:
        //               UNION
        //               / | \
        //  CONCATENATION  c  CONCATENATION
        //       /   \          /     \
        //      a     b        d       e
        val s1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val s2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("d", "e"))
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("c"))
        root.children.add(0, s1)
        root.children.add(s2)

        assertFalse(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.UNION))
        assertFalse(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.CONCATENATION))
        assertFalse(root.containsSubtreesThroughOperator(s2, s1, LogicalPlan.CONCATENATION))
    }

    @Test
    fun partialChildrenCheck() {

        // Given s1 & s2:
        //    CONCATENATION              CONCATENATION
        //       /    \                     /    \
        //      b      c                   d      e
        //
        // Root:
        //            CONCATENATION
        //            /  |  |  |  \
        //           a   b  c  d   e
        val s1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("b", "c"))
        val s2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("d", "e"))
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "c", "d", "e"))

        assertTrue(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.CONCATENATION))
        assertFalse(root.containsSubtreesThroughOperator(s2, s1, LogicalPlan.CONCATENATION))
        assertFalse(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.UNION))
        assertFalse(root.containsSubtreesThroughOperator(s2, s1, LogicalPlan.UNION))
    }

    @Test
    fun unflattenedParseTreesCheck() {

        // Given s1 & s2:
        //    CONCATENATION        c
        //       /    \
        //      a      b
        //
        // Root:
        //            CONCATENATION
        //              /      \
        //       CONCATENATION  c
        //          /    \
        //         a      b
        val s1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        val s2 = LogicalPlan(mock<Query>(), LogicalPlan.LEAF, Edge("c"))
        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("c"))
        root.children.add(0, s1)

        // MAKE SURE YOU HAVE FLATTENED THE TREE YOURSELF, BECAUSE THIS RETURNS FALSE... AS EXPECTED
        assertFalse(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.CONCATENATION))
        assertFalse(root.containsSubtreesThroughOperator(s2, s1, LogicalPlan.CONCATENATION))
        assertFalse(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.UNION))
        assertFalse(root.containsSubtreesThroughOperator(s2, s1, LogicalPlan.UNION))

        root.flatten()
        assertTrue(root.containsSubtreesThroughOperator(s1, s2, LogicalPlan.CONCATENATION))
    }
}