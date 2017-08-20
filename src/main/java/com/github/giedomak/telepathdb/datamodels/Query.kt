package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan

data class Query(val telepathDB: TelepathDB, val input: String) {

    var logicalPlan: ParseTree? = null
    var physicalPlan: PhysicalPlan? = null
    var results: PathStream? = null

    fun parseInput() {
        logicalPlan = telepathDB.staticParser.parse(this)
    }

    fun generatePhysicalPlan() {
        physicalPlan = telepathDB.planner.generate(logicalPlan!!)
    }

    fun evaluate() {
        results = PathStream(telepathDB.evaluationEngine.evaluate(physicalPlan!!))
    }

    fun stream(): PathStream {
        return PathStream(results!!.paths)
    }

}