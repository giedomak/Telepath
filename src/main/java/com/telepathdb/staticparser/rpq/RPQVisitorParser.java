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
    QueryOperatorVisitor queryOperatorVisitor = new QueryOperatorVisitor();
    ParseTree parseTree = queryOperatorVisitor.visit(parser.query());
    return parseTree;
  }

  /**
   * Our query visitor inline class This class extends the RPQBaseVisitor class and contains the
   * appropriate visitor methods, "listeners". These methods will be "fired" if we visit one of the
   * rules from the RPQ grammar while visiting the ANTLR ParseTree. The overridden methods from
   * RPQBaseVisitor allow us to check if we are dealing with unary operators like Kleene star or
   * Plus, or binary operators like Union or Concatenation for example.
   */
  private static class QueryOperatorVisitor extends RPQBaseVisitor<ParseTree> {

    @Override
    public ParseTree visitQueryOperator(@NotNull RPQParser.QueryOperatorContext ctx) {
      String className = ctx.getText();

      System.out.println("--> YAY: " + className);
      System.out.println("--> YAY2: " + ctx.unaryOperator().getText());
      System.out.println("--> YAY2: " + ctx.unaryOperator().PLUS());
      System.out.println("--> YAY2: " + RPQParser.PLUS);

//      MethodVisitor methodVisitor = new MethodVisitor();
//      List<Method> methods = ctx.method()
//          .stream()
//          .map(method -> method.accept(methodVisitor))
//          .collect(toList());
      return new ParseTree();
    }
  }

//  private static class MethodVisitor extends RPQBaseVisitor<ParseTree> {
//    @Override
//    public Method visitMethod(@NotNull RPQParser.MethodContext ctx) {
//      String methodName = ctx.methodName().getText();
//      InstructionVisitor instructionVisitor = new InstructionVisitor();
//      List<Instruction> instructions = ctx.instruction()
//          .stream()
//          .map(instruction -> instruction.accept(instructionVisitor))
//          .collect(toList());
//      return new Method(methodName, instructions);
//    }
//  }
//
//  private static class InstructionVisitor extends RPQBaseVisitor<ParseTree> {
//
//    @Override
//    public Instruction visitInstruction(@NotNull RPQParser.InstructionContext ctx) {
//      String instructionName = ctx.getText();
//      return new Instruction(instructionName);
//    }
//  }
}
