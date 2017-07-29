/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.planner;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.staticparser.StaticParserRPQTest;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PlannerTest {

  @Test
  public void generatesSimplePhysicalPlan() {
    // Generate the actual ParseTree
    ParseTree input = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.CONCATENATION, Arrays.asList("a", "b"));
    ParseTree actual = Planner.INSTANCE.generate(input);

    // Generate the expected ParseTree
    ParseTree expected = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.LOOKUP, Arrays.asList("a", "b"));

    assertEquals(expected, actual);
  }

  @Test
  public void generatesMultiLevelPhysicalPlan() {
    // Input:
    //       CONCATENATION
    //        /      \
    //       a   CONCATENATION
    //              /   \
    //             b     c
    ParseTree child = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.CONCATENATION, Arrays.asList("b", "c"));
    ParseTree root = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.CONCATENATION, Arrays.asList("a"));
    root.setChild(1, child);

    // Parse the input
    ParseTree actual = Planner.INSTANCE.generate(root);

    // Generate the expected ParseTree
    //         LOOKUP
    //        /  |  \
    //       a   b   c
    ParseTree expected = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.LOOKUP, Arrays.asList("a", "b", "c"));

    assertEquals(expected, actual);
  }
}
