/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.evaluationengine

import com.github.giedomak.telepath.datamodels.graph.PathStream
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan

/**
 * Evaluate a physical-plan in order to get results from the path-index.
 */
object SimpleEvaluationEngine : EvaluationEngine {

    override fun evaluate(physicalPlan: PhysicalPlan): PathStream {
        return physicalPlan.physicalOperator!!.evaluate()
    }

}
