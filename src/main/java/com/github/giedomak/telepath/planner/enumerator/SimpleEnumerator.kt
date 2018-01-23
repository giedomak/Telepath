package com.github.giedomak.telepath.planner.enumerator

import com.github.giedomak.telepath.datamodels.plans.LogicalPlan
import com.github.giedomak.telepath.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepath.physicaloperators.PhysicalOperator

/**
 * The SimpleEnumerator will enumerate all physical operators we support for a given logical operator.
 */
object SimpleEnumerator : Enumerator {

    /**
     * Merge two physical plans by all the physical operators we support for the given [logicalOperator].
     *
     * @param tree1 First tree we have to merge.
     * @param tree2 Second tree we have to merge.
     * @param logicalOperator Merge the given trees with all physical operators for this given logical logicalOperator.
     * @return All physical plans where the given trees are merged with all physical operators we support for the given [logicalOperator].
     */
    override fun enumerate(tree1: PhysicalPlan, tree2: PhysicalPlan, logicalOperator: Int): Sequence<PhysicalPlan> {

        return when (logicalOperator) {

            LogicalPlan.CONCATENATION -> enumerateConcatenation(tree1, tree2)

            LogicalPlan.UNION -> listOf(tree1.merge(tree2, PhysicalOperator.UNION)).asSequence()

            else -> TODO("I see you like to enumerate")
        }

    }

    /**
     * Enumerate all the physical operators we support for CONCATENATION.
     */
    private fun enumerateConcatenation(tree1: PhysicalPlan, tree2: PhysicalPlan): Sequence<PhysicalPlan> {

        val physicalPlans = mutableListOf<PhysicalPlan>()

        // Check if an INDEX_LOOKUP is applicable.
        // Let's say we've got:
        //
        //       INDEX_LOOKUP   INDEX_LOOKUP
        //            |              |
        //            a              b
        //
        // We merge them with the INDEX_LOOKUP operator as root:
        //
        //        INDEX_LOOKUP
        //         /      \
        // INDEX_LOOKUP  INDEX_LOOKUP
        //      |             |
        //      a             b
        //
        // And flatten:
        //
        //      INDEX_LOOKUP
        //        /    \
        //       a      b
        val plan = tree1.merge(tree2, PhysicalOperator.INDEX_LOOKUP)
        val flattened = plan.flatten()

        // If the height of this tree is 1 (max number of edges to any leaf), AND the number of children
        // is smaller or equal to the k-value of our index, we can do an INDEX_LOOKUP!
        if (flattened.height() == 1) {
            if (flattened.children.size <= flattened.query.telepath.kPathIndex.k) {
                physicalPlans.add(flattened)
            }
//            plan.operator = PhysicalOperator.SORT_MERGE_JOIN
//            plan.children.first().children.forEach { it.leaf = it.leaf!!.inverse() }
//            plan.children.first().children.reverse()
//            physicalPlans.add(plan)
        }

        // Don't forget to enumerate all the JOIN_OPERATORS
//        PhysicalOperator.JOIN_OPERATORS.forEach {
//            physicalPlans.add(tree1.merge(tree2, it))
//        }

        physicalPlans.add(tree1.merge(tree2, PhysicalOperator.HASH_JOIN))

        return physicalPlans.asSequence()
    }

}
