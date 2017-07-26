package com.telepathdb.staticparser;

import com.telepathdb.datamodels.ParseTree;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StaticParserRPQTest {

  private StaticParser staticParserRPQ = new StaticParserRPQ();

  @Test
  public void inputBecomesAParseTree() {
    // Parse the input into a ParseTree
    ParseTree actual = staticParserRPQ.parse("a");

    // Create the expected ParseTree
    ParseTree expected = new ParseTree(true);
    expected.setLeaf("a");

    // Then
    assertEquals(expected, actual);
  }

  @Test
  public void concatenationInput() {
    // Parse the input into a ParseTree
    ParseTree actual = staticParserRPQ.parse("a/b");

    // Create the expected ParseTree
    ParseTree expected = new ParseTree(true);
    expected.setOperator(ParseTree.CONCATENATION);

    ParseTree a = new ParseTree();
    a.setLeaf("a");

    ParseTree b = new ParseTree();
    b.setLeaf("b");

    expected.setChild(0, a);
    expected.setChild(1, b);

    // Then
    assertEquals(expected, actual);

    // Modify
    ParseTree c = new ParseTree();
    c.setLeaf("c");
    
    expected.setChild(1, c);

    // Then
    assertNotEquals(expected, actual);
  }
}
