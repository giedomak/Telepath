/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.ParseTreePrinter;
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
    ParseTree parseTree = rpqVisitorParser.parse(input);

    // Print the parsed ParseTree
    ParseTreePrinter.printParseTree(parseTree);

  }
}
