package com.telepathdb.staticparser.rpq;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.staticparser.rpq.antlr.RPQBaseVisitor;
import com.telepathdb.staticparser.rpq.antlr.RPQLexer;
import com.telepathdb.staticparser.rpq.antlr.RPQParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Parse the RPQ ANTLR4 AST into our ParseTree model
 */
public class RPQVisitorParser extends RPQBaseVisitor<ParseTree> {

  public ParseTree parse(String RPQSourceCode) {
    CharStream charStream = new ANTLRInputStream(RPQSourceCode);
    RPQLexer lexer = new RPQLexer(charStream);
    TokenStream tokens = new CommonTokenStream(lexer);
    RPQParser parser = new RPQParser(tokens);

    QueryOperatorVisitor queryOperatorVisitor = new QueryOperatorVisitor();
    ParseTree parseTree = queryOperatorVisitor.visit(parser.query());
    return parseTree;
  }

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
