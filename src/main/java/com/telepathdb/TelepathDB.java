/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb;

import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.kpathindex.KPathIndexInMemory;
import com.telepathdb.staticparser.StaticParser;
import com.telepathdb.staticparser.StaticParserSparql;

class TelepathDB {

  private static StaticParser staticParser;
  private static KPathIndex kPathIndex;

  public static void main(String[] args) {

    // We want to use the Sparql query language
    staticParser = new StaticParserSparql();

    // We want to use the InMemory version of the KPathIndex
    kPathIndex = new KPathIndexInMemory();

    System.out.println("TelepathDB is up and running");
  }
}
