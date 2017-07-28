/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.utilities.Logger;
import com.telepathdb.datamodels.utilities.ParseTreePrinter;
import com.telepathdb.staticparser.rpq.RPQVisitorParser;

/**
 * RPQ query language
 */
public class StaticParserRPQ implements StaticParser {

  @Override
  public ParseTree parse(String input) {

    Logger.INSTANCE.info("Your input: " + input);
    RPQVisitorParser rpqVisitorParser = new RPQVisitorParser();

    ParseTree parseTree = rpqVisitorParser.parse(input);

    // Print the parsed ParseTree
    ParseTreePrinter.INSTANCE.printParseTree(parseTree);

    return parseTree;

  }
}
