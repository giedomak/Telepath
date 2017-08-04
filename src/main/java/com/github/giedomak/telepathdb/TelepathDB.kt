/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb

import com.github.giedomak.telepathdb.datamodels.utilities.Logger
import com.github.giedomak.telepathdb.datamodels.utilities.ParseTreePrinter
import com.github.giedomak.telepathdb.datamodels.utilities.UnionPuller
import com.github.giedomak.telepathdb.evaluationengine.EvaluationEngine
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.kpathindex.utilities.GMarkImport
import com.github.giedomak.telepathdb.kpathindex.utilities.KExtender
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.planner.Planner
import com.github.giedomak.telepathdb.staticparser.StaticParser
import com.github.giedomak.telepathdb.staticparser.StaticParserRPQ
import java.io.IOException
import java.util.*
import kotlin.streams.toList

internal object TelepathDB {

    private var staticParser: StaticParser? = null
    private var kPathIndex: KPathIndex? = null
    private var evaluationEngine: EvaluationEngine? = null

    private val scanner = Scanner(System.`in`)

    /**
     * Main loop. Setup the environment and wait for user-input.
     *
     * @param args Not needed.
     * @throws IOException
     */
    @JvmStatic fun main(args: Array<String>) {

        // Setup our environment
        setup()

        // Start TelepathDB and listen for query input
        start()
    }

    /**
     * Listen for query input and gather results.
     */
    private fun start() {

        while (true) {

            // Retrieve input from the user until we retrieve 'END'
            val input = getUserInput(scanner)

            val startTime = System.currentTimeMillis()

            // Parse the input
            val parseTree = staticParser!!.parse(input)

            // Pull unions out and split the parsetree into an array of multiple UNION-less parsetrees
            val parseTrees = UnionPuller.parse(parseTree)

            Logger.debug("UNION-less parsetrees:")

            val physicalPlans = parseTrees.stream()
                    .map { Planner.generate(it) }
                    .toList()

            for ((index, origParseTree) in parseTrees.withIndex()) {
                Logger.debug("ParseTree " + index)
                ParseTreePrinter.printParseTree(origParseTree)
                Logger.debug("PhysicalPlan " + index)
                ParseTreePrinter.printParseTree(physicalPlans[index])
            }

            // Evaluate the physical plan
            val results = physicalPlans.stream()
                    .map { evaluationEngine!!.evaluate(it) }
                    .toList()

            // Print the results
            val collectedResults = results.stream().flatMap { it }.toList()
            val endTime = System.currentTimeMillis()

            Logger.info(">>>>> Results:")

            collectedResults.stream().limit(10).forEach { Logger.info(it) }
            if (collectedResults.size > 10) {
                Logger.info("And " + (collectedResults.size - 10) + " more......")
            }

            Logger.info("Number of results: " + collectedResults.size + ", after " + (endTime - startTime) + " ms")
            Logger.info("----------------------------")

            // Clear the intermediate results in our memory and cache
            MemoryManager.clear()
        }
    }

    /**
     * Retrieve input from the user until we retrieve 'END'.
     */
    private fun getUserInput(scanner: Scanner): String {

        // Print which parser we are using
        Logger.info("We are using " + staticParser!!.javaClass.simpleName + ", enter your query and finish with the keyword END on a newline:")

        var input = ""
        var value = scanner.nextLine()
        while (value.indexOf("END") == -1) {
            input += value
            value = scanner.nextLine()
        }

        return input
    }

    /**
     * Setup our modules and database and print the time needed.
     */
    private fun setup() {

        val startTime = System.currentTimeMillis()

        // Init everything we need
        setupModules()

        setupDatabase("/Users/giedomak/Dropbox/graphInstances/graph10k.txt", 3)

        // We're alive!
        val endTime = System.currentTimeMillis()
        Logger.debug("----------------------------")
        Logger.debug("TelepathDB is up and running after " + (endTime - startTime) + " ms")

    }

    /**
     * Setup modules with the implementation of the interfaces we choose.
     */
    private fun setupModules() {

        // Setup the staticParser
        // staticParser = new StaticParserSparql();
        staticParser = StaticParserRPQ

        // We want to use the InMemory version of the KPathIndex
        kPathIndex = KPathIndexInMemory()

        // Setup the Evaluation Engine with the kPathIndex
        evaluationEngine = EvaluationEngine(kPathIndex!!)
    }

    /**
     * Setup the database and extend to our k-value.
     */
    private fun setupDatabase(gMarkFile: String, k: Int) {

        try {
            // Import test dataset
            GMarkImport.run(kPathIndex!!, gMarkFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Extend from k=1 to k
        KExtender.run(kPathIndex!!, k)
    }
}
