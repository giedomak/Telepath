/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTreeTest
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlanTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PlannerTest {

    @Test
    fun generatesSimplePhysicalPlan() {
        // Generate the actual ParseTree
        val input = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a", "b"))
        val actual = Planner.generate(input)

        // Generate the expected ParseTree
        val expected = PhysicalPlanTest.generatePhysicalPlan(PhysicalPlan.LOOKUP, listOf("a", "b"))

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
        val child = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("b", "c"))
        val root = ParseTreeTest.create1LevelParseTree(ParseTree.CONCATENATION, listOf("a"))
        root.setChild(1, child)

        // Parse the input
        val actual = Planner.generate(root)

        // Generate the expected ParseTree
        //         LOOKUP
        //        /  |  \
        //       a   b   c
        val expected = PhysicalPlanTest.generatePhysicalPlan(PhysicalPlan.LOOKUP, listOf("a", "b", "c"))

        assertEquals(expected, actual)
    }
}
