package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.utilities.Logger

data class Query(val telepathDB: TelepathDB, val input: String) {

    var logicalPlan: LogicalPlan? = null
    var flattenedLogicalPlan: LogicalPlan? = null
    var physicalPlan: PhysicalPlan? = null
    var results: PathStream? = null

    private var startTime = System.currentTimeMillis()
    private var endTime = System.currentTimeMillis()

    fun parseInput() {
        logicalPlan = telepathDB.staticParser.parse(this)
        Logger.debug("Logical plan:")
        logicalPlan!!.print()
    }

    fun flattenLogicalPlan() {
        flattenedLogicalPlan = logicalPlan!!.clone().flatten()
        Logger.debug("Flattened logical plan:")
        flattenedLogicalPlan!!.print()
    }

    fun generatePhysicalPlan() {
        physicalPlan = telepathDB.planner.generate(flattenedLogicalPlan!!)
        Logger.debug("Physical plan:")
        physicalPlan!!.print()
    }

    fun evaluate() {
        results = telepathDB.evaluationEngine.evaluate(physicalPlan!!)

        endTime = System.currentTimeMillis()
    }

    fun printResults() {

        Logger.debug(">>>>> Estimated number of results: " + physicalPlan!!.cardinality)
        Logger.info(">>>>> Results limited to 10:")

        results!!.paths.limit(10).forEach { Logger.info(it) }

        Logger.info("Query evaluation in " + (endTime - startTime) + " ms")
        Logger.info("----------------------------")
    }
}