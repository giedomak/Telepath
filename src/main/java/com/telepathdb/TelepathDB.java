/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.utilities.Logger;
import com.telepathdb.datamodels.utilities.ParseTreePrinter;
import com.telepathdb.evaluationengine.EvaluationEngine;
import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.kpathindex.KPathIndexInMemory;
import com.telepathdb.kpathindex.utilities.GMarkImport;
import com.telepathdb.kpathindex.utilities.KExtender;
import com.telepathdb.memorymanager.MemoryManager;
import com.telepathdb.planner.Planner;
import com.telepathdb.staticparser.StaticParser;
import com.telepathdb.staticparser.StaticParserRPQ;
import com.telepathdb.staticparser.UnionPuller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TelepathDB {

  private static StaticParser staticParser;
  private static KPathIndex kPathIndex;
  private static EvaluationEngine evaluationEngine;

  /**
   * Main loop. Setup the environment and wait for user-input
   *
   * @param args Not needed
   * @throws IOException
   */
  public static void main(String[] args) {

    // Setup our environment
    setup();

    // Start TelepathDB and listen for query input
    start();
  }

  /**
   * Listen for query input and gather results
   */
  private static void start() {

    Scanner in = new Scanner(System.in);

    while (true) {

      // Print which parser we are using
      Logger.INSTANCE.info("We are using " + staticParser.getClass().getSimpleName() + ", enter your query and finish with the keyword END on a newline:");

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

      // Pull unions out and split the parsetree into an array of multiple UNION-less parsetrees
      List<ParseTree> parseTrees = UnionPuller.parse(parseTree);

      Logger.INSTANCE.debug("UNION-less parsetrees:");

      List<ParseTree> physicalPlans = parseTrees.stream()
          .map(Planner::generate)
          .collect(Collectors.toList());

      for (int i = 0; i < parseTrees.size(); i++) {
        Logger.INSTANCE.debug("ParseTree " + i);
        ParseTreePrinter.printParseTree(parseTrees.get(i));
        Logger.INSTANCE.debug("PhysicalPlan " + i);
        ParseTreePrinter.printParseTree(physicalPlans.get(i));
      }

      // Evaluate the physical plan
      List<Stream<Path>> results = physicalPlans.stream()
          .map(evaluationEngine::evaluate)
          .collect(Collectors.toList());

      // Print the results
      List<Path> collectedResults = results.stream().flatMap(t -> t).collect(Collectors.toList());
      long endTime = System.currentTimeMillis();

      Logger.INSTANCE.info(">>>>> Results:");

      collectedResults.stream().limit(10).forEach(Logger.INSTANCE::info);
      if (collectedResults.size() > 10) {
        Logger.INSTANCE.info("And " + (collectedResults.size() - 10) + " more......");
      }

      Logger.INSTANCE.info("Number of results: " + collectedResults.size() + ", after " + (endTime - startTime) + " ms");
      Logger.INSTANCE.info("----------------------------");

      // Clear the intermediate results in our memory and cache
      MemoryManager.clear();
    }

  }

  /**
   * Setup our modules and database and print the time needed.
   */
  private static void setup() {

    long startTime = System.currentTimeMillis();

    // Init everything we need
    setupModules();

    setupDatabase("/Users/giedomak/Dropbox/graphInstances/graph10k.txt", 3);

    // We're alive!
    long endTime = System.currentTimeMillis();
    Logger.INSTANCE.debug("----------------------------");
    Logger.INSTANCE.debug("TelepathDB is up and running after " + (endTime - startTime) + " ms");

  }

  /**
   * Setup modules with the implementation of the interfaces we choose.
   */
  private static void setupModules() {

    // Setup the staticParser
    // staticParser = new StaticParserSparql();
    staticParser = new StaticParserRPQ();

    // We want to use the InMemory version of the KPathIndex
    kPathIndex = new KPathIndexInMemory();

    // Setup the Evaluation Engine with the kPathIndex
    evaluationEngine = new EvaluationEngine(kPathIndex);
  }

  /**
   * Setup the database and extend to our k-value
   */
  private static void setupDatabase(String gMarkFile, int k) {

    try {
      // Import test dataset
      GMarkImport.run(kPathIndex, gMarkFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Extend from k=1 to k
    KExtender.run(kPathIndex, k);
  }
}
