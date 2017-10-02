/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb

import com.github.giedomak.telepathdb.cardinalityestimation.CardinalityEstimation
import com.github.giedomak.telepathdb.cardinalityestimation.SynopsisCardinalityEstimation
import com.github.giedomak.telepathdb.costmodel.AdvancedCostModel
import com.github.giedomak.telepathdb.costmodel.CostModel
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.evaluationengine.EvaluationEngine
import com.github.giedomak.telepathdb.evaluationengine.SimpleEvaluationEngine
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.kpathindex.KPathIndexDisk
import com.github.giedomak.telepathdb.kpathindex.utilities.KExtender
import com.github.giedomak.telepathdb.kpathindex.utilities.LUBMImport
import com.github.giedomak.telepathdb.memorymanager.MemoryManager
import com.github.giedomak.telepathdb.memorymanager.SimpleMemoryManager
import com.github.giedomak.telepathdb.planner.DynamicProgrammingPlanner
import com.github.giedomak.telepathdb.planner.Planner
import com.github.giedomak.telepathdb.planner.enumerator.Enumerator
import com.github.giedomak.telepathdb.planner.enumerator.SimpleEnumerator
import com.github.giedomak.telepathdb.staticparser.StaticParser
import com.github.giedomak.telepathdb.staticparser.StaticParserRPQ
import com.github.giedomak.telepathdb.utilities.Logger
import java.io.IOException
import java.util.*

object TelepathDB {

    // ------ MODULES ------

    var staticParser: StaticParser = StaticParserRPQ
    var kPathIndex: KPathIndex = KPathIndexDisk()
    var evaluationEngine: EvaluationEngine = SimpleEvaluationEngine
    var costModel: CostModel = AdvancedCostModel
    var cardinalityEstimation: CardinalityEstimation = SynopsisCardinalityEstimation(kPathIndex = kPathIndex)
    var planner: Planner = DynamicProgrammingPlanner
    var enumerator: Enumerator = SimpleEnumerator
    var memoryManager: MemoryManager = SimpleMemoryManager

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

            // Parse the input
            query.parseInput()

            // Flatten the logical plan
            query.flattenLogicalPlan()

            // Generate the physical plan
            query.generatePhysicalPlan()

            // Evaluate the physical plan
            query.evaluate()

            // Print the results
            query.printResults()

            // Clear the intermediate results in our memory and cache
            memoryManager.clear()
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

//        GMarkImport.run(kPathIndex, "src/test/resources/cite.txt")
        LUBMImport.run(kPathIndex, "/Users/giedomak/Documents/Apps/lubm-uba/Universities.nt")

        // Extend the index and synopsis to k = 2
        KExtender.run(kPathIndex, 2)

        // Clear the results in our memory and cache
        memoryManager.clear()

//        kPathIndex.k = 1

        // We're alive!
        val endTime = System.currentTimeMillis()
        Logger.debug("----------------------------")
        Logger.debug("TelepathDB is up and running after " + (endTime - startTime) + " ms")
    }

}
