/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser;


import com.telepathdb.staticparser.rpq.RPQVisitorParser;

/**
 * RPQ query language
 */
public class StaticParserRPQ implements StaticParser {

  @Override
  public void input() {

  }

  @Override
  public void parse(String input) {

    System.out.println("Your input: " + input);
    RPQVisitorParser rpqVisitorParser = new RPQVisitorParser();

    // Catch the IllegalStateException, since we don't want further propogation when this occurs
    try {
      rpqVisitorParser.parse(input);
    } catch (IllegalStateException exception) {
      System.out.println(exception);
    }

  }


//  private void parseInput(String input) {
//
//    // Inspiration: http://stackoverflow.com/a/15051200/3238444
//
//    // Get our lexer
//    RPQLexer rpqLexer = new RPQLexer(new ANTLRInputStream(input));
//
//    // Pass the tokens to the parser
//    RPQParser rpqParser = new RPQParser(new CommonTokenStream(rpqLexer));
//
//    // Attach an error listener in order to raise errors when needed
//    rpqParser.addErrorListener(new BaseErrorListener() {
//      @Override
//      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
//        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
//      }
//    });
//
//    // Attach a listener to parsing events in order to specify behavior
//    RPQBaseListener listener = new RPQBaseListener() {
//      @Override
//      public void enterQuery(RPQParser.QueryContext ctx) {
//        System.out.println(ctx.getText());
//      }
//    };
//
//    // Specify our entry point
//    RPQParser.QueryContext queryContext = rpqParser.query();
//    System.out.println(queryContext.toStringTree());
//
//    // Open the parse tree in a GUI
//    new TreeViewer(Arrays.asList(rpqParser.getRuleNames()), queryContext).open();
//
//    // Walk it and attach our listener
//    // ParseTreeWalker walker = new ParseTreeWalker();
//    // walker.walk(listener, queryContext);
//  }
}
