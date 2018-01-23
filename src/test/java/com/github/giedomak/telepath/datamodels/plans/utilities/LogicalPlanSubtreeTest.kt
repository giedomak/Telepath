/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.datamodels.plans.utilities

import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.LogicalPlanTest
import org.junit.Assert
import org.junit.Test

class LogicalPlanSubtreeTest {

    @Test
    fun subtreesOfSize2() {
        // Input:
        //       CONCATENATION
        //        /   |   \
        //       a    b  UNION
        //                / \
        //               c   d
        val child = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("c", "d"))
        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b"))
        input.children.add(child)

        // Expected:
        //   CONCATENATION   UNION
        //       /  \         / \
        //      a    b       c   d
        val expected = listOf(
                LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b")),
                LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("c", "d"))
        )

        val actual = input.subtreesOfSize(2)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize2Complex() {
        // Input:
        //         CONCATENATION
        //        /  |     |  |  \
        //       a  UNION  e  f   g
        //          / | \
        //         b  c  d
        val child = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("b", "c", "d"))
        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "e", "f", "g"))
        input.children.add(1, child)

        // Expected:
        //     UNION   UNION    UNION   CONCATENATION    CONCATENATION
        //      / \     / \      / \       /   \            /   \
        //     b   c   c   b    d   b     e     f          f     g
        // TODO: Union should include 'b' - 'd' subtree
        val expected = listOf(
                LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("b", "c")),
                LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("c", "d")),
//                LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("d", "b")),
                LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("e", "f")),
                LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("f", "g"))
        )

        val actual = input.subtreesOfSize(2)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun subtreesOfSize3() {
        // Input:
        //        CONCATENATION
        //        /  |    |   \
        //       a   b  UNION  d
        //               / \
        //              c   d
        val child = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("c", "d"))
        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("a", "b", "d"))
        input.children.add(2, child)

        // Expected:
        //      CONCATENATION    CONCATENATION
        //         /   \             /   \
        //        b   UNION       UNION   d
        //             / \         / \
        //            c   d       c   d
        val expected1 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("b"))
        expected1.children.add(child)
        val expected2 = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("d"))
        expected2.children.add(0, child)
        val expected = listOf(expected1, expected2)

        val actual = input.subtreesOfSize(3)

        Assert.assertEquals(expected, actual)
    }
}
