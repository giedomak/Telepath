package com.github.giedomak.telepathdb.evaluationengine

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

interface EvaluationEngine {

    fun evaluate(physicalPlan: PhysicalPlan): PathStream

}