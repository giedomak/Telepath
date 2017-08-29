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

        const val INDEX_LOOKUP = 1

        const val HASH_JOIN = 2
        const val NESTED_LOOP_JOIN = 3

        const val UNION = 4

        // ------ COLLECTIONS -------

        val JOIN_OPERATORS = listOf(HASH_JOIN, NESTED_LOOP_JOIN)

        // ------ FUNCTIONS -------

        fun getPhysicalOperator(physicalPlan: PhysicalPlan): PhysicalOperator? {

            return when (physicalPlan.operator) {

                LEAF -> null

                INDEX_LOOKUP -> IndexLookup(physicalPlan)

                HASH_JOIN -> HashJoin(physicalPlan)
                NESTED_LOOP_JOIN -> NestedLoopJoin(physicalPlan)

                UNION -> Union(physicalPlan)

                else -> TODO("Gotta catch em all")
            }
        }
    }
}