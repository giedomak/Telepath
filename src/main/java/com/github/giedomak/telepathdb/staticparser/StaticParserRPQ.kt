/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.staticparser

import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.utilities.Logger
import com.github.giedomak.telepathdb.staticparser.rpq.RPQVisitorParser

/**
 * RPQ query language.
 */
object StaticParserRPQ : StaticParser {

    override fun parse(input: String): ParseTree {

        Logger.info("Your input: " + input)
        val rpqVisitorParser = RPQVisitorParser()

        val parseTree = rpqVisitorParser.parse(input)

        // Print the parsed ParseTree
        parseTree.print()

        return parseTree
    }
}
