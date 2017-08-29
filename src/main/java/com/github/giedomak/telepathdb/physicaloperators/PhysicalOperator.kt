package com.github.giedomak.telepathdb.physicaloperators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan

/**
 * Interface for physical operators.
 *
 * @property physicalPlan The physical plan holds information regarding the sets on which to operate on.
 * @property firstChild The first set of data to operate on.
 * @property lastChild The last set of data to operate on.
 */
interface PhysicalOperator {

    val physicalPlan: PhysicalPlan

    val firstChild get() = physicalPlan.children.first().physicalOperator!!
    val lastChild get() = physicalPlan.children.last().physicalOperator!!

    /**
     * Evaluates the physical operator and produces a PathStream.
     *
     * @return PathStream with the results of the evaluation.
     */
    fun evaluate(): PathStream

    /**
     * Calculates the cost of the physical operation.
     *
     * @return The cost of the physical operation.
     */
    fun cost(): Long

    /**
     * Delegate cardinality to the physical plan.
     */
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

        /**
         * Convert the operators constants to an actual [PhysicalOperator] instance.
         *
         * @param physicalPlan which holds the operator constant.
         * @return The PhysicalOperator instance which has knowledge of the physical plan.
         */
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