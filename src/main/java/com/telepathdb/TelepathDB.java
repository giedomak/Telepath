/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.utilities.Logger;
import com.telepathdb.evaluationengine.EvaluationEngine;
import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.kpathindex.KPathIndexInMemory;
import com.telepathdb.kpathindex.utilities.GMarkImport;
import com.telepathdb.kpathindex.utilities.KExtender;
import com.telepathdb.memorymanager.MemoryManager;
import com.telepathdb.staticparser.StaticParser;
import com.telepathdb.staticparser.StaticParserRPQ;
import com.telepathdb.staticparser.StaticParserSparql;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TelepathDB {

  private static StaticParser staticParser;
  private static KPathIndex kPathIndex;
  private static EvaluationEngine evaluationEngine;

  public static void main(String[] args) throws IOException {

    long startTime = System.currentTimeMillis();

    // Init everything we need
    setupModules();

    // Import test dataset
    GMarkImport.run(kPathIndex, "/Users/giedomak/Dropbox/graphInstances/graph10k.txt");

    // Extend from k=1 to k=3
    KExtender.run(kPathIndex, 3);

    // We're alive!
    long endTime = System.currentTimeMillis();
    Logger.debug("----------------------------");
    Logger.debug("TelepathDB is up and running after " + (endTime - startTime) + " ms");

    // Start TelepathDB and listen for query input
    start();

  }

  /**
   * Listen for query input and gather results
   */
  protected static void start() throws IOException {

    Scanner in = new Scanner(System.in);

    while (true) {

      // Print which parser we are using
      Logger.info("We are using " + staticParser.getClass().getSimpleName() + ", enter your query and finish with the keyword END on a newline:");

      // Retrieve input from the user until we retrieve 'END'
      String input = "";
      String val = in.nextLine();
      while (val.indexOf("END") == -1) {
        input += val;
        val = in.nextLine();
      }

      long startTime = System.currentTimeMillis();

      // Parse the input
      ParseTree parseTree = staticParser.parse(input);

      // Evaluate the physical plan
      Stream<Path> results = evaluationEngine.evaluate(parseTree);

      // Print the results
      List<Path> collectedResults = results.collect(Collectors.toList());
      long endTime = System.currentTimeMillis();
      Logger.info("Results:");
      collectedResults.stream().limit(10).forEach(Logger::info);
      if (collectedResults.size() > 10)
        Logger.info("And more......");
      Logger.info("Number of results: " + collectedResults.size() + ", after " + (endTime - startTime) + " ms");
      Logger.info("----------------------------");

      // Clear the intermediate results in our memory and cache
      MemoryManager.clear();
    }

  }

  /**
   * Setup modules with the implementation of the interfaces we choose.
   */
  private static void setupModules() throws IOException {

    // We want to use the Sparql query language
    staticParser = new StaticParserSparql();
    staticParser = new StaticParserRPQ();

    // We want to use the InMemory version of the KPathIndex
    kPathIndex = new KPathIndexInMemory();

    // Setup the Evaluation Engine
    evaluationEngine = new EvaluationEngine(kPathIndex);
  }
}
