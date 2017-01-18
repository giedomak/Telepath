/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser;


import com.telepathdb.staticparser.sparql.SparqlBaseListener;
import com.telepathdb.staticparser.sparql.SparqlLexer;
import com.telepathdb.staticparser.sparql.SparqlParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import java.util.Arrays;

/**
 * Sparql query language
 */
public class StaticParserSparql implements StaticParser {

  @Override
  public void input() {

  }

  @Override
  public void parse(String input) {

    System.out.println("Your input: " + input);

    // Catch the IllegalStateException, since we don't want further propogation when this occurs
    try {
      parseInput(input);
    } catch (IllegalStateException exception) {
    }

  }

  private void parseInput(String input) {

    // Inspiration: http://stackoverflow.com/a/15051200/3238444

    // Get our lexer
    SparqlLexer sparqlLexer = new SparqlLexer(new ANTLRInputStream(input));

    // Pass the tokens to the parser
    SparqlParser sparqlParser = new SparqlParser(new CommonTokenStream(sparqlLexer));

    // Attach an error listener in order to raise errors when needed
    sparqlParser.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
      }
    });

    // Attach a listener to parsing events in order to specify behavior
    SparqlBaseListener listener = new SparqlBaseListener() {
      @Override
      public void enterQuery(SparqlParser.QueryContext ctx) {
        System.out.println(ctx.getText());
      }
    };

    // Specify our entry point
    SparqlParser.QueryContext queryContext = sparqlParser.query();
    System.out.println(queryContext.toStringTree());

    // Open the parse tree in a GUI
    new TreeViewer(Arrays.asList(sparqlParser.getRuleNames()), queryContext).open();

    // Walk it and attach our listener
    // ParseTreeWalker walker = new ParseTreeWalker();
    // walker.walk(listener, queryContext);
  }
}
