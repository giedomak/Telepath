/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator

class KPathIndexCardinalityEstimation(kPathIndex: KPathIndexInMemory) : CardinalityEstimation {

    private val statisticsStore = kPathIndex.getStatisticsStore()

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
            1
        }
    }

    /**
     * Returns the cardinality of a given physicalPlan.
     *
     * This method will recursively calculate the cardinality for its children in order to get the cardinality
     * for the root.
     *
     * Assumption:
     *
     * @param physicalPlan The root of the tree for which we want to get the cardinality.
     * @return
     */
    override fun getCardinality(physicalPlan: PhysicalPlan): Long {

        return when (physicalPlan.operator) {

            PhysicalOperator.INDEX_LOOKUP -> getCardinality(physicalPlan.pathIdOfChildren())

            in PhysicalOperator.JOIN_OPERATORS -> {
                val d1 = getCardinality(physicalPlan.children.first())
                val d2 = getCardinality(physicalPlan.children.last())
                val selectivity = 1 / (Math.max(d1, d2))
                d1 * d2 * selectivity
            }

            else -> TODO("Whoooops")
        }
    }
}
