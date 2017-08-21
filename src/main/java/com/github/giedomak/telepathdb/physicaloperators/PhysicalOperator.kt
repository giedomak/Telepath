package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

interface PhysicalOperator {

    val physicalPlan: PhysicalPlan

    val firstChild get() = physicalPlan.children.first().physicalOperator!!
    val lastChild get() = physicalPlan.children.last().physicalOperator!!

    fun evaluate(): PathStream

    fun cost(): Long

    fun cardinality(): Long {
        return physicalPlan.cardinality()
    }

    companion object {

        // ------ CONSTANTS ------

        const val LEAF = 0
        const val INDEXLOOKUP = 1
        const val HASHJOIN = 2
        const val NESTEDLOOPJOIN = 3

        // ------ FUNCTIONS -------

        fun getPhysicalOperator(physicalPlan: PhysicalPlan): PhysicalOperator? {

            return when (physicalPlan.operator) {

                LEAF -> null
                INDEXLOOKUP -> IndexLookup(physicalPlan)
                HASHJOIN -> HashJoin(physicalPlan)
                NESTEDLOOPJOIN -> NestedLoopJoin(physicalPlan)

                else -> TODO("Gotta catch em all")
            }
        }
    }
}