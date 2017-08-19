/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.parsetree.PhysicalPlan

object CardinalityEstimation {

    private val statisticsStore = TelepathDB.kPathIndex.getStatisticsStore()

    /**
     * Returns the cardinality of a given [pathId].
     */
    fun getCardinality(pathId: Long): Long {
        return try {
            statisticsStore.getCardinality(pathId)
        } catch (e: NullPointerException) {
            1
        }
    }

    fun getCardinality(physicalPlan: PhysicalPlan): Long {

        return when (physicalPlan.operator) {

            PhysicalPlan.LOOKUP -> getCardinality(physicalPlan.pathIdOfLookup())

            PhysicalPlan.HASHJOIN -> {
                val d1 = getCardinality(physicalPlan.children.first() as PhysicalPlan)
                val d2 = getCardinality(physicalPlan.children.last() as PhysicalPlan)
                val selectivity = 1 / (Math.max(d1, d2))
                d1 * d2 * selectivity
            }

            PhysicalPlan.NESTEDLOOPJOIN -> {
                val d1 = getCardinality(physicalPlan.children.first() as PhysicalPlan)
                val d2 = getCardinality(physicalPlan.children.last() as PhysicalPlan)
                val selectivity = 1 / (Math.max(d1, d2))
                d1 * d2 * selectivity
            }

            else -> TODO("Whoooops")
        }
    }
}
