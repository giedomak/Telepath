package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.utilities.Logger

data class Query(val telepathDB: TelepathDB, val input: String) {

    var logicalPlan: LogicalPlan? = null
    var physicalPlan: PhysicalPlan? = null
    var results: PathStream? = null

    fun parseInput() {
        logicalPlan = telepathDB.staticParser.parse(this)
        Logger.debug("Logical plan:")
        logicalPlan!!.print()
    }

    fun generatePhysicalPlan() {
        physicalPlan = telepathDB.planner.generate(logicalPlan!!)
        Logger.debug("Physical plan:")
        physicalPlan!!.print()
    }

    fun evaluate() {
        results = telepathDB.evaluationEngine.evaluate(physicalPlan!!)
    }

    fun stream(): PathStream {
        return results!!
    }

}