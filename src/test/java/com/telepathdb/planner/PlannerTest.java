package com.telepathdb.planner;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.staticparser.StaticParserRPQTest;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PlannerTest {

  @Test
  public void generateSimplePhysicalPlan() {
    // Generate the actual ParseTree
    ParseTree input = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.CONCATENATION, Arrays.asList("a", "b"));
    ParseTree actual = Planner.generate(input);

    // Generate the expected ParseTree
    ParseTree expected = StaticParserRPQTest.Companion.create1LevelParseTree(
        ParseTree.LOOKUP, Arrays.asList("a", "b"));

    assertEquals(expected, actual);
  }
}
