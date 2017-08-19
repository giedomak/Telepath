package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan

data class Query(val input: String) {

    var logicalPlan: ParseTree? = null
    var physicalPlans = emptyList<PhysicalPlan>()

}