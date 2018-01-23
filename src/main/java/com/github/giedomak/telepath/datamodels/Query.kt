package com.github.giedomak.telepath.datamodels

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.utilities.Logger

/**
 * Query model which is responsible for the-life-of-a-query.
 *
 * @property telepath Reference to the Telepath module.
 * @property input The input as given by the user.
 */
data class Query(val telepath: Telepath, val input: String) {

    private var logicalPlan: LogicalPlan? = null
     var flattenedLogicalPlan: LogicalPlan? = null
     var physicalPlan: PhysicalPlan? = null
    private var results: PathStream? = null

    private var startTime = System.currentTimeMillis()
    private var endTime = System.currentTimeMillis()

    fun parseInput() {
        val start = System.currentTimeMillis()
        logicalPlan = telepath.staticParser.parse(this)
        val ms = System.currentTimeMillis() - start

        Logger.debug("Logical plan: ($ms ms)")
        logicalPlan!!.print()
    }

    fun flattenLogicalPlan() {
        val start = System.currentTimeMillis()
        flattenedLogicalPlan = logicalPlan!!.clone().flatten()
        val ms = System.currentTimeMillis() - start

        Logger.debug("Flattened logical plan: ($ms ms)")
        flattenedLogicalPlan!!.print()
    }

    fun generatePhysicalPlan(): Long {
        val start = System.currentTimeMillis()
        physicalPlan = telepath.planner.generate(flattenedLogicalPlan!!)
        val ms = System.currentTimeMillis() - start

        Logger.debug("Physical plan: ($ms ms)")
        physicalPlan!!.print()

        return ms
    }

    fun evaluate(): Long {
        val start = System.currentTimeMillis()
        results = telepath.evaluationEngine.evaluate(physicalPlan!!)
        val ms = System.currentTimeMillis() - start

        Logger.debug("Evaluation step done in ($ms ms)")

        endTime = System.currentTimeMillis()

        return ms
    }

    fun printEstimate() {
        Logger.debug(">>>>> Estimated number of results: " + physicalPlan!!.cardinality())
    }

    fun printCount(force: Boolean = false) {
        // If the results were materialized, we actually get a supplier. Meaning we can access the stream again.
        if (results!!.materialize || force) {
            Logger.debug(">>>>> Actual number of results: " + results!!.paths.count())
        }
    }

    fun printResults(maxSize: Long = 10) {

        Logger.info(">>>>> Results limited to $maxSize:")
        results!!.paths.limit(maxSize).forEach { Logger.info(it) }

        Logger.info("Query evaluation in " + (endTime - startTime) + " ms")
        Logger.info("----------------------------")
    }
}