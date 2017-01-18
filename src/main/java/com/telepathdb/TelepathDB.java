/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb;

import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.kpathindex.KPathIndexInMemory;
import com.telepathdb.kpathindex.utilities.GMarkImport;
import com.telepathdb.staticparser.StaticParser;
import com.telepathdb.staticparser.StaticParserSparql;

import java.io.IOException;
import java.util.Scanner;

class TelepathDB {

  private static StaticParser staticParser;
  private static KPathIndex kPathIndex;
  private static GMarkImport gMarkImport;

  public static void main(String[] args) throws IOException {

    // Init everything we need
    setupModules();

    // Import test dataset
    long imported = gMarkImport.doImport("/Users/giedomak/Dropbox/graphInstances/graph10K.txt");
    System.out.println("Imported paths: " + imported);
//    System.out.println(kPathIndex.search(new PathPrefix(null, )));

    System.out.println("TelepathDB is up and running");

    // Start TelepathDB and listen for query input
    start();

  }

  /**
   * Listen for query input and gather results
   */
  private static void start() {

    Scanner in = new Scanner(System.in);

    while(true) {

      // Gather input from the user
      System.out.println("We are using the " + staticParser.getClass().getSimpleName() + " parser, enter your query and end with the keyword END:");
      String input = "";
      String val = in.nextLine();
      while(val.indexOf("END") == -1) {
        input += val;
        val = in.nextLine();
      }

      // Parse the input
      staticParser.parse(input);
    }

  }

  /**
   * Setup modules with the implementation of the interfaces we choose.
   */
  private static void setupModules() throws IOException {

    // We want to use the Sparql query language
    staticParser = new StaticParserSparql();

    // We want to use the InMemory version of the KPathIndex
    kPathIndex = new KPathIndexInMemory();

    // We might want to use the GMarkImporter
    gMarkImport = new GMarkImport(kPathIndex);

  }
}
