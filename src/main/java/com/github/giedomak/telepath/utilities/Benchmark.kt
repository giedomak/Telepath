/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the Telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.utilities

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.cardinalityestimation.SynopsisCardinalityEstimation
import com.github.giedomak.telepath.datamodels.Query
import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.kpathindex.KPathIndexDisk
import com.github.giedomak.telepath.kpathindex.utilities.AdvogatoImport
import com.github.giedomak.telepath.kpathindex.utilities.KExtender
import com.github.giedomak.telepath.kpathindex.utilities.LUBMImport

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.SimpleDateFormat
import java.util.*

object Benchmark {

    // ----- QUERIES -----

    private val LUBM_Q1 = Pair("LUBM_Q1", "undergraduateDegreeFrom / !subOrganizationOf / !memberOf")
    private val LUBM_Q2 = Pair("LUBM_Q2", "advisor / teacherOf / !takesCourse")
    private val LUBM_Q3 = Pair("LUBM_Q3", "!headOf / worksFor / !subOrganizationOf")
    private val LUBM_Q4 = Pair("LUBM_Q4", "!headOf / worksFor / subOrganizationOf")

    private val LUBM_Q5 = Pair("LUBM_Q5", "advisor / teacherOf / !takesCourse / advisor")
    private val LUBM_Q6 = Pair("LUBM_Q6", "undergraduateDegreeFrom / !subOrganizationOf / !memberOf / advisor")
    private val LUBM_Q7 = Pair("LUBM_Q7", "!worksFor / teacherOf / !takesCourse / advisor")

    private val LUBM_Q8 = Pair("LUBM_Q8", "!teacherOf / undergraduateDegreeFrom / !subOrganizationOf / !memberOf / advisor")
    private val LUBM_Q9 = Pair("LUBM_Q9", "advisor / teacherOf / !takesCourse / advisor / teacherOf")

    private val LUBM = listOf(LUBM_Q1, LUBM_Q2, LUBM_Q3, LUBM_Q4, LUBM_Q5, LUBM_Q6, LUBM_Q7, LUBM_Q8, LUBM_Q9)

    private val ADVOGATO_Q1 = Pair("ADVOGATO_Q1", "apprentice / apprentice / apprentice")
    private val ADVOGATO_Q2 = Pair("ADVOGATO_Q2", "journeyer / journeyer / journeyer")
    private val ADVOGATO_Q3 = Pair("ADVOGATO_Q3", "master / master / master")
    private val ADVOGATO_Q4 = Pair("ADVOGATO_Q4", "apprentice / journeyer / master")

    private val ADVOGATO_Q5 = Pair("ADVOGATO_Q5", "apprentice / apprentice / apprentice / !journeyer")
    private val ADVOGATO_Q6 = Pair("ADVOGATO_Q6", "apprentice / journeyer / !apprentice / master")
    private val ADVOGATO_Q7 = Pair("ADVOGATO_Q7", "master / apprentice / !master / journeyer")

    private val ADVOGATO_Q8 = Pair("ADVOGATO_Q8", "apprentice / apprentice / apprentice / apprentice / apprentice")
    private val ADVOGATO_Q9 = Pair("ADVOGATO_Q9", "apprentice / journeyer / !master / !apprentice / master")

    private val ADVOGATO = listOf(ADVOGATO_Q1, ADVOGATO_Q2, ADVOGATO_Q3, ADVOGATO_Q4, ADVOGATO_Q5, ADVOGATO_Q6, ADVOGATO_Q7, ADVOGATO_Q8, ADVOGATO_Q9)

    // ----- BENCHMARK CONFIG ------

    private val LUBMDataset = "/Users/giedomak/Documents/Apps/lubm-uba/10/Universities.nt"
    private val AdvogatoDataset = "src/test/resources/advogato-graph-2014-07-07.dot"

    private val kPathIndexLocationLUBM = File("/Users/giedomak/Desktop/10k2/")
    private val kPathIndexLocationAdvogato = File("/Users/giedomak/Desktop/Advogatok2/")

    @JvmStatic
    fun main(args: Array<String>?) {

//        while (true) {

            setupLUBM()
        runLUBMCardinalityEstimation()
//
//            // Run the experiments
//            runLUBM(false)
//
//            Telepath.kPathIndex.k = 1
//            runLUBM(false)

            setupAdvogato()

            runAdvogatoCardinalityEstimation()

//            runAdvogatoCardinality()

//            setupLUBM()
//
//            runLUBMCardinality()

//            runAdvogato(false)
//
//            Telepath.kPathIndex.k = 1
//            runAdvogato(false)

//        }
    }

    private fun extendK() {
        // Make sure our Paths are known in our PathIdentifierStore, plus this constructs the Synopsis
        KExtender.run(Telepath.kPathIndex, 2, true)

        (Telepath.cardinalityEstimation as SynopsisCardinalityEstimation).synopsis.done()
    }

    private fun setupLUBM() {
        Telepath.pathIdentifierStore.clear()
        Node.clear()
        // Re-use the index we already constructed
        Telepath.kPathIndex = KPathIndexDisk(dir = kPathIndexLocationLUBM)
        // The cardinality estimator needs to know this new index
        Telepath.cardinalityEstimation = SynopsisCardinalityEstimation(kPathIndex = Telepath.kPathIndex)

        // Make sure our Nodes and Paths are known in our PathIdentifierStore and NodeIdentifierStore
        LUBMImport.run(Telepath.kPathIndex, LUBMDataset, true)

        extendK()

        // Clear the results in our memory and cache
        Telepath.memoryManager.clear()
    }

    private fun setupAdvogato() {
        Telepath.pathIdentifierStore.clear()
        Node.clear()
        // Re-use the index we already constructed
        Telepath.kPathIndex = KPathIndexDisk(dir = kPathIndexLocationAdvogato)
        // The cardinality estimator needs to know this new index
        Telepath.cardinalityEstimation = SynopsisCardinalityEstimation(kPathIndex = Telepath.kPathIndex)

        // Make sure our Nodes and Paths are known in our PathIdentifierStore and NodeIdentifierStore
        AdvogatoImport.run(Telepath.kPathIndex, AdvogatoDataset, true)

        extendK()

        // Clear the results in our memory and cache
        Telepath.memoryManager.clear()
    }

    private fun runLUBM(first: Boolean = true): List<Pair<String, String>> {

        val results = mutableListOf<Pair<String, String>>()

        for ((key, value) in LUBM) {
            runQuery(key, value, results, first)
        }

        Logger.debug(results)

        return results
    }

    private fun runAdvogato(first: Boolean = true): List<Pair<String, String>> {

        val results = mutableListOf<Pair<String, String>>()

        for ((key, value) in ADVOGATO) {
            runQuery(key, value, results, first)
        }

        Logger.debug(results)

        return results
    }

    private fun runLUBMCardinality() {

        for ((key, value) in LUBM) {
            runCardinality(key, value)
        }

    }

    private fun runAdvogatoCardinality() {

        for ((key, value) in ADVOGATO) {
            runCardinality(key, value)
        }

    }

    private fun runAdvogatoCardinalityEstimation() {

        val yay = mutableListOf<Pair<String, Long>>()

        for ((key, value) in ADVOGATO) {
            yay.add(runCardinalityEstimation(key, value))
        }

        Logger.debug(yay)

    }

    private fun runLUBMCardinalityEstimation() {

        val yay = mutableListOf<Pair<String, Long>>()

        for ((key, value) in LUBM) {
            yay.add(runCardinalityEstimation(key, value))
        }

        Logger.debug(yay)

    }

    private fun runCardinalityEstimation(key: String, value: String) : Pair<String, Long> {
        // Retrieve input from the user until we retrieve 'END'
        val query = Query(Telepath, value)

        // Parse the input
        query.parseInput()

        // Flatten the logical plan
        query.flattenLogicalPlan()

        query.generatePhysicalPlan()

        Logger.debug("Result $key: ${query.physicalPlan!!.cardinality()}")

        return Pair(key, query.physicalPlan!!.cardinality())
    }

    private fun runCardinality(key: String, value: String) {


        // Retrieve input from the user until we retrieve 'END'
        val query = Query(Telepath, value)

        // Parse the input
        query.parseInput()

        // Flatten the logical plan
        query.flattenLogicalPlan()

        query.generatePhysicalPlan()

        val start = System.currentTimeMillis()

//        query.physicalPlan!!.physicalOperator!!.evaluate().paths.count().toInt()

//        val yay = System.currentTimeMillis() - start

        val yay = intermediateResult(query.physicalPlan!!)

        Logger.debug("Yay: $yay")

        // Generate the physical plan
        val plans = CardinalityBenchmark.generate(query.flattenedLogicalPlan!!).toList()

        Logger.debug("Plans: ${plans.size}")
        plans.forEach { it.print() }

//        val mss = mutableListOf<Long>()
//
//        plans.forEach {
//
//            val start2 = System.currentTimeMillis()
//
//            it.physicalOperator!!.evaluate().paths.count()
//
//            val ms2 = System.currentTimeMillis() - start2
//            mss.add(ms2)
//
//            Logger.debug("Result: $ms2")
//
//        }

        val intermediateResults = hashMapOf<PhysicalPlan, Int>()

        // Evaluate the physical plan
        plans.forEach {

            val result = intermediateResult(it)

            intermediateResults.put(it, result)

            Logger.debug("Result: $result")

        }

//        val sorted = mss.sorted()
        val sorted = intermediateResults.values.sorted()

        Logger.debug("THE CHOSEN ONE: $yay")
        Logger.debug("Cheap: ${sorted.first()}")
        Logger.debug("Expensive: ${sorted.last()}")
        Logger.debug("Average: ${sorted.average()}")
        Logger.debug("Plans: ${sorted.size}")
        val index = sorted.indexOf(yay)
        Logger.debug("Index: $index")
        Logger.debug("Sorted: $sorted")


        // Print the results
//        if (first) query.printResults(1)
//        if (!first) query.printCount(true)

        val ms = System.currentTimeMillis() - start

        query.printEstimate()
        Logger.debug("Query evaluation in $ms")

        val timestamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())

        Files.write(
                Paths.get("cardinality.txt"),
                ("$timestamp: \"Query $key: $ms. THE CHOSEN ONE: $yay, " +
                        "Cheap: ${sorted.first()}, Expensive: ${sorted.last()}, " +
                        "Average: ${sorted.average()}, Plans: ${sorted.size}" +
                        ", Index: $index, Sorted: $sorted \r\n").toByteArray(),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
        )

        // Clear the intermediate results in our memory and cache
        Telepath.memoryManager.clear()

    }

    private fun intermediateResult(pp: PhysicalPlan): Int {
        return pp.postOrderTraversal().filter { !it.isLeaf }.sumBy { it.physicalOperator!!.evaluate().paths.count().toInt() }
    }

    private fun runQuery(key: String, value: String, results: MutableList<Pair<String, String>>, first: Boolean): MutableList<Pair<String, String>> {

        // Record the timings
        val physicalPlanTimings = mutableListOf<Long>()
        val evaluationTimings = mutableListOf<Long>()
        val queryEvaluationTimings = mutableListOf<Long>()

        val k = Telepath.kPathIndex.k

        // Run 20 times
        for (i in 1..20) {

            Logger.debug("Run $i of Query $key. First: $first, results: $results")

            val start = System.currentTimeMillis()

            // Retrieve input from the user until we retrieve 'END'
            val query = Query(Telepath, value)

            // Parse the input
            query.parseInput()

            // Flatten the logical plan
            query.flattenLogicalPlan()

            // Generate the physical plan
            physicalPlanTimings.add(query.generatePhysicalPlan())

            // Evaluate the physical plan
            evaluationTimings.add(query.evaluate())

            // Print the results
            if (first) query.printResults(1)
            if (!first) query.printCount(true)

            val ms = System.currentTimeMillis() - start
            queryEvaluationTimings.add(ms)

            query.printEstimate()
            Logger.debug("Query evaluation in $ms")

            val timestamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())

            Files.write(
                    Paths.get("runs.txt"),
                    "$timestamp: \"Run $i of Query $key: $ms. K = $k, First: $first \r\n".toByteArray(),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            )

            // Clear the intermediate results in our memory and cache
            Telepath.memoryManager.clear()
        }

        val physicalPlanResult = physicalPlanTimings.sorted().drop(2).reversed().drop(2).average()
        val evaluationResult = evaluationTimings.sorted().drop(2).reversed().drop(2).average()
        val queryEvaluationResult = queryEvaluationTimings.sorted().drop(2).reversed().drop(2).average()


        val result = Pair(key, "$k, $first, $physicalPlanResult, $evaluationResult, $queryEvaluationResult")
        results.add(result)

        val timestamp = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())

        Files.write(
                Paths.get("benchmark.txt"),
                "$timestamp: $result \r\n".toByteArray(),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
        )

        Logger.debug("$key: $k: $first: Physical plans average: $physicalPlanResult")
        Logger.debug("$key: $k: $first: Evaluations average: $evaluationResult")
        Logger.debug("$key: $k: $first: Query evaluations average: $queryEvaluationResult")

        return results
    }

}
