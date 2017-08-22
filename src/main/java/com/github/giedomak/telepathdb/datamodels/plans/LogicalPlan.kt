/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan.Companion
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan.Companion.CONCATENATION
import com.github.giedomak.telepathdb.datamodels.plans.utilities.MultiTreeContainment
import com.github.giedomak.telepathdb.datamodels.plans.utilities.LogicalPlanSubtree
import com.github.giedomak.telepathdb.datamodels.plans.utilities.LogicalPlanUnionPuller

/**
 * Data-structure to represent the given user input, and physical plans.
 *
 * This data-structure is used for both representing the user input, and for physical plans.
 * It basically is a binary-tree. Meaning its children are in turn also ParseTrees.
 *
 * User input will get parsed through ANTLR into a parseTree by the [StaticParserRPQ][com.telepathdb.staticparser.StaticParserRPQ].
 *
 * The [DynamicProgrammingPlanner][com.telepathdb.planner.DynamicProgrammingPlanner] will parse such a parseTree from user input, into a physical plan.
 * These physical plans can have slightly different operators and payloads in regard to a parseTree generated from user input.
 *
 * @property id An ID given to a parseTree in order to make the life of the [MemoryManager][com.telepathdb.memorymanager.MemoryManager] easier.
 * @property isRoot Boolean value indicating if this parseTree is the root of the binary-tree.
 * @property children Ordered list with the children of this parseTree, which are also parseTrees.
 * @property operator An [Int] representing the operator. See our [Companion] for these constants, i.e. [CONCATENATION].
 * @property isLeaf Boolean value indicating if this parseTree is a leaf.
 * @property nodeRepresentation Get a String representing the leaf or operator. I.e. `a` or `CONCATENATION[3]`.
 * @property leaf The payload when this parseTree is a leaf, given as an [Edge].
 * @constructor Create a non-root empty LogicalPlan.
 */
class LogicalPlan(
        query: Query,
        override var operator: Int = 0,
        leaf: Edge? = null
) : AbstractMultiTree<LogicalPlan>(query, leaf) {

    override val operatorName get() = SYMBOLIC_NAMES[operator]

    /**
     * Delegate parse-tree-union-pulling to our [LogicalPlanUnionPuller].
     */
    fun pullUnions(): List<LogicalPlan> {
        return LogicalPlanUnionPuller.parse(this)
    }

    fun getSize(): Int {
        return LogicalPlanSubtree.getSize(this)
    }

    /**
     * Delegate parse-tree-sizing to our [LogicalPlanSubtree].
     */
    fun subtreesOfSize(targetSize: Int): List<LogicalPlan> {
        return LogicalPlanSubtree.subtreesOfSize(this, targetSize)
    }

    /**
     * Delegate parse-tree-containment to our [MultiTreeContainment].
     */
    fun containsSubtreesThroughOperator(s1: LogicalPlan, s2: LogicalPlan, operatorId: Int): Boolean {
        return MultiTreeContainment.containsSubtreesThroughOperator(this, s1, s2, operatorId)
    }

    /**
     * Check if the current tree contains a given operator.
     *
     * @param operatorId The operator constant (e.g. LogicalPlan.UNION) to check for containment in the tree.
     * @return Boolean indicating if the tree contains the operator.
     */
    fun contains(operatorId: Int): Boolean {
        return postOrderTraversal().any { it.operator == operatorId }
    }

    fun mergeAndFlatten(tree: LogicalPlan, operator: Int): LogicalPlan {
        val root = LogicalPlan(query, operator)
        root.children.add(this.clone())
        root.children.add(tree.clone())
        return root.flatten()
    }

    //
    // ---------------- COMPANION ----------------
    //

    companion object {

        // Our public constants identifying our symbolic names
        const val LEAF = 0
        const val KLEENE_STAR = 1
        const val PLUS = 2
        const val CONCATENATION = 3
        const val UNION = 4

        val OPERATORS = listOf(KLEENE_STAR, PLUS, CONCATENATION, UNION)

        private val SYMBOLIC_NAMES = arrayOf(null, "KLEENE_STAR", "PLUS", "CONCATENATION", "UNION")

    }

}
