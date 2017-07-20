/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser.rpq;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.staticparser.rpq.antlr.RPQBaseVisitor;
import com.telepathdb.staticparser.rpq.antlr.RPQLexer;
import com.telepathdb.staticparser.rpq.antlr.RPQParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Parse the RPQ ANTLR4 AST into our internal ParseTree model
 * We use the Visitor approach instead of the Listener approach
 * Inspiration: http://jakubdziworski.github.io/java/2016/04/01/antlr_visitor_vs_listener.html
 */
public class RPQVisitorParser extends RPQBaseVisitor<ParseTree> {

  /**
   * This is the main method which we need to call if we want to parse a RPQ.
   * Here we define to start parsing the input using the query() rule.
   *
   * @param RPQSourceCode The complete RPQ formatted as a String
   * @return ParseTree The complete ParseTree after recursively traversing the complete ANTLR AST.
   */
  public ParseTree parse(String RPQSourceCode) {

    // Setup the lexer and parser
    RPQLexer lexer = new RPQLexer(new ANTLRInputStream(RPQSourceCode));
    RPQParser parser = new RPQParser(new CommonTokenStream(lexer));

    // Here we define to start parsing our query with the query() rule
    RPQQueryVisitor RPQQueryVisitor = new RPQQueryVisitor();
    ParseTree parseTree = RPQQueryVisitor.visit(parser.query());
    parseTree.setRoot(true);
    return parseTree;
  }

  /**
   * Our query visitor inline class This class extends the RPQBaseVisitor class and contains the
   * appropriate visitor methods, "listeners". These methods will be "fired" if we visit one of the
   * rules from the RPQ grammar while visiting the ANTLR ParseTree. The overridden methods from
   * RPQBaseVisitor allow us to check if we are dealing with unary operators like Kleene star or
   * Plus, or binary operators like Union or Concatenation for example.
   */
  private static class RPQQueryVisitor extends RPQBaseVisitor<ParseTree> {

    @Override
    public ParseTree visitUnaryExpression(@NotNull RPQParser.UnaryExpressionContext ctx) {

      ParseTree result = new ParseTree();

      if (ctx.unaryOperator().PLUS() != null) {
        // PLUS
        result.setOperator(ParseTree.PLUS);
      } else if (ctx.unaryOperator().KLEENE_STAR() != null) {
        // KLEENE STAR
        result.setOperator(ParseTree.KLEENE_STAR);
      }

      // Recurse on the left-side for which this operator was intended
      result.setChild(0, visit(ctx.query()));

      return result;
    }

    @Override
    public ParseTree visitBinaryExpression(@NotNull RPQParser.BinaryExpressionContext ctx) {

      ParseTree result = new ParseTree();

      if (ctx.binaryOperator().UNION() != null) {
        // UNION
        result.setOperator(ParseTree.UNION);
      } else if (ctx.binaryOperator().CONJUNCTION() != null) {
        // CONCATENATION
        result.setOperator(ParseTree.CONCATENATION);
      }

      // Recurse on the left-side and the right-side for which this operator was intended
      result.setChild(0, visit(ctx.query(0))); // First occurence of query
      result.setChild(1, visit(ctx.query(1))); // Second occurence of query

      return result;
    }

    @Override
    public ParseTree visitLeaf(@NotNull RPQParser.LeafContext ctx) {

      ParseTree result = new ParseTree();
      result.setOperator(ParseTree.LEAF);
      result.setLeaf(ctx.LABEL().getText());

      return result;
    }

    @Override
    public ParseTree visitParenthesis(@NotNull RPQParser.ParenthesisContext ctx) {
      return visit(ctx.query());
    }
  }
}
