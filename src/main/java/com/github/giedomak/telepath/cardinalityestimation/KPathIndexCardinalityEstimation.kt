/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.cardinalityestimation

import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.kpathindex.KPathIndexDisk
import com.github.giedomak.telepath.physicaloperators.PhysicalOperator
import com.github.giedomak.telepath.utilities.Logger

class KPathIndexCardinalityEstimation(kPathIndex: KPathIndex) : CardinalityEstimation {

    // We can only use this CardinalityEstimation if the KPathIndexInMemory is used.
    private val statisticsStore = (kPathIndex as KPathIndexDisk).getStatisticsStore()

    /**
     * Returns the cardinality of a given [pathId] using the StatisticsStore from our kPathIndex.
     *
     * @param pathId The pathId of the path we want the cardinality for.
     * @return The cardinality of the given pathId or 1 when non-existent.
     */
    fun getCardinality(pathId: Long): Long {
        return try {
            statisticsStore.getCardinality(pathId)
        } catch (e: NullPointerException) {
//            throw IllegalStateException("UNKNOWN CARDINALITY FOR PATH $pathId")
//            Logger.error("UNKNOWN CARDINALITY FOR PATH $pathId")
            1
        }
    }

    /**
     * Returns the cardinality of a given physicalPlan.
     *
     * This method will recursively calculate the cardinality for its children in order to get the cardinality
     * for the root.
     *
     * @param physicalPlan The root of the tree for which we want to get the cardinality.
     * @return The cardinality of the given physicalPlan.
     */
    override fun getCardinality(physicalPlan: PhysicalPlan): Long {

        return when (physicalPlan.operator) {

            PhysicalOperator.INDEX_LOOKUP -> getCardinality(physicalPlan.pathIdOfChildren())

            in PhysicalOperator.JOIN_OPERATORS -> {
                val d1 = getCardinality(physicalPlan.children.first())
                val d2 = getCardinality(physicalPlan.children.last())
                Math.max(d1, d2)
            }

            PhysicalOperator.UNION -> {
                getCardinality(physicalPlan.children.first()) + getCardinality(physicalPlan.children.last())
            }

            else -> TODO("You forgot one!")
        }
    }
}
