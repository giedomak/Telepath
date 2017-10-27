package com.github.giedomak.telepath.evaluationengine

import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

interface EvaluationEngine {

    fun evaluate(physicalPlan: PhysicalPlan): PathStream

}