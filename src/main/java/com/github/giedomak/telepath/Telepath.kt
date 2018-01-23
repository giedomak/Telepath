/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath

import com.github.giedomak.telepath.cardinalityestimation.CardinalityEstimation
import com.github.giedomak.telepath.cardinalityestimation.SynopsisCardinalityEstimation
import com.github.giedomak.telepath.costmodel.AdvancedCostModel
import com.github.giedomak.telepath.costmodel.CostModel
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.evaluationengine.EvaluationEngine
import com.github.giedomak.telepath.evaluationengine.SimpleEvaluationEngine
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.kpathindex.KPathIndexDisk
import com.github.giedomak.telepath.kpathindex.utilities.AdvogatoImport
import com.github.giedomak.telepath.kpathindex.utilities.KExtender
import com.github.giedomak.telepath.memorymanager.MemoryManager
import com.github.giedomak.telepath.memorymanager.SimpleMemoryManager
import com.github.giedomak.telepath.planner.DynamicProgrammingPlanner
import com.github.giedomak.telepath.planner.Planner
import com.github.giedomak.telepath.planner.enumerator.Enumerator
import com.github.giedomak.telepath.planner.enumerator.SimpleEnumerator
import com.github.giedomak.telepath.staticparser.StaticParser
import com.github.giedomak.telepath.staticparser.StaticParserRPQ
import com.github.giedomak.telepath.utilities.Logger
import java.io.IOException
import java.util.*

object Telepath {

    // ------ MODULES ------

    var staticParser: StaticParser = StaticParserRPQ
//    var kPathIndex: KPathIndex = KPathIndexDisk(dir = File("/Users/giedomak/Desktop/10k2/"))
//    var kPathIndex: KPathIndex = KPathIndexDisk(dir = File("/Users/giedomak/Desktop/Advogatok2/"))
    var kPathIndex: KPathIndex = KPathIndexDisk()
//    var kPathIndex: KPathIndex = KPathIndexInMemory()
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

        // Start Telepath and listen for query input
        start()
    }

    /**
     * Listen for query input and gather results.
     */
    fun start() {

        while (true) {

            try {

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
                query.printEstimate()
//                query.printCount(true)
                query.printResults()

                // Clear the intermediate results in our memory and cache
                memoryManager.clear()

            } catch (exception: Exception) {
                if (exception is IllegalArgumentException) {
                    // Re-raise the exception when we are dealing with IllegalArgumentException.
                    // This is used to test the integration of this function.
                    throw exception
                }
                exception.printStackTrace()
            }
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

//        GMarkImport.run(kPathIndex, "src/test/resources/graph10K.txt")
//        GMarkImport.run(kPathIndex, "src/test/resources/cite.txt")
//        LUBMImport.run(kPathIndex, "/Users/giedomak/Documents/Apps/lubm-uba/10/Universities.nt", true)
        AdvogatoImport.run(kPathIndex, "src/test/resources/advogato-graph-2014-07-07.dot", true)

        Logger.debug("Number of nodes: " + Node.numberOfNodes())

        // Extend the index and synopsis to k = 2
        KExtender.run(kPathIndex, 2, true)
        kPathIndex.k = 1

        // Clear the results in our memory and cache
        memoryManager.clear()

        // We're alive!
        val endTime = System.currentTimeMillis()
        Logger.debug("----------------------------")
        Logger.debug("Telepath is up and running after " + (endTime - startTime) + " ms")
    }

}
