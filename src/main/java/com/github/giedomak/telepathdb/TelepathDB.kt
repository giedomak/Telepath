/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb

import com.github.giedomak.telepathdb.cardinalityestimation.KPathIndexCardinalityEstimation
import com.github.giedomak.telepathdb.costmodel.AdvancedCostModel
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.evaluationengine.SimpleEvaluationEngine
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.kpathindex.utilities.GMarkImport
import com.github.giedomak.telepathdb.kpathindex.utilities.KExtender
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.planner.DynamicProgrammingPlanner
import com.github.giedomak.telepathdb.planner.Planner
import com.github.giedomak.telepathdb.planner.enumerator.SimpleEnumerator
import com.github.giedomak.telepathdb.staticparser.StaticParserRPQ
import com.github.giedomak.telepathdb.utilities.Logger
import java.io.IOException
import java.util.*
import kotlin.streams.toList

object TelepathDB {

    // ------ MODULES ------

    var staticParser = StaticParserRPQ
    val kPathIndex = KPathIndexInMemory()
    var evaluationEngine = SimpleEvaluationEngine
    val costModel = AdvancedCostModel
    var cardinalityEstimation = KPathIndexCardinalityEstimation(kPathIndex)
    var planner: Planner = DynamicProgrammingPlanner
    val enumerator = SimpleEnumerator

    // ------ STORES -------

    val pathIdentifierStore = PathIdentifierStore

    // ------ PRIVATE ------

    private val scanner = Scanner(System.`in`)

    /**
     * Main loop. Setup the environment and wait for user-input.
     *
     * @param args Not needed.
     * @throws IOException
     */
    @JvmStatic
    fun main(args: Array<String>?) {

        // Setup our environment
        setup()

        // Start TelepathDB and listen for query input
        start()
    }

    /**
     * Listen for query input and gather results.
     */
    fun start() {

        while (true) {

            // Retrieve input from the user until we retrieve 'END'
            val query = Query(this, getUserInput(scanner))

            val startTime = System.currentTimeMillis()

            // Parse the input
            query.parseInput()

            // Generate the physical plan
            query.generatePhysicalPlan()

            // Evaluate the physical plan
            query.evaluate()

            // Print the results
            val collectedResults = query.stream().paths.toList()
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
    fun getUserInput(scanner: Scanner): String {

        // Print which parser we are using
        Logger.info("We are using " + staticParser.javaClass.simpleName + ", enter your query and finish with the keyword END on a newline:")

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

        setupDatabase("/Users/giedomak/Dropbox/graphInstances/graph10k.txt", 3)

        // We're alive!
        val endTime = System.currentTimeMillis()
        Logger.debug("----------------------------")
        Logger.debug("TelepathDB is up and running after " + (endTime - startTime) + " ms")
    }

    /**
     * Setup the database and extend to our k-value.
     */
    private fun setupDatabase(gMarkFile: String, k: Int) {

        try {
            // Import test dataset
            GMarkImport.run(kPathIndex, gMarkFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Extend from k=1 to k
        KExtender.run(kPathIndex, k)
    }
}
