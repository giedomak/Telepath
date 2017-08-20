/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.plans.utilities

import com.github.giedomak.telepathdb.datamodels.plans.AbstractMultiTree
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan

object MultiTreeContainment {

    /**
     * Given a root [LogicalPlan], check if [s1] and [s2] are contained as subtrees in [root] through the given [operatorId].
     *
     * Since we could be dealing with partial subtrees, we have to flatten again...
     *
     * Given s1 & s2:
     *
     *    CONCATENATION              CONCATENATION
     *       /    \                     /    \
     *      a      b                   c      d
     *
     * Tree:
     *
     *            CONCATENATION
     *              /  |  |  \
     *             a   b  c   d
     *
     * containsSublistOfChildren(tree, s1, s2) should equal true.
     *
     * @param root The root LogicalPlan we'll be using to check containment of given subtrees.
     * @param s1 The first subtree.
     * @param s2 The second subtree.
     * @param operatorId The operator through which [s1] and [s2] are connected.
     * @return True if [s1] and [s2] are connected through [operatorId] and contained in [root].
     */
    fun containsSubtreesThroughOperator(root: LogicalPlan, s1: LogicalPlan, s2: LogicalPlan, operatorId: Int): Boolean {

        val subtrees = mutableListOf<AbstractMultiTree<*>>()

        // If we are dealing with a subtree that is rooted with the same operator as the one we will be checking,
        // we only have to consider its children.
        for (subtree in listOf(s1, s2)) {
            if (subtree.operator == operatorId) subtrees.addAll(subtree.children) else subtrees.add(subtree)
        }

        // Check for each node which is equal to our operator, if our subtrees list is
        // directly contained in its children and its indices are next to each other.
        return root.postOrderTraversal()
                .filter { it.operator == operatorId }
                .any { containsSublistOfChildren(it.children, subtrees) }
    }

    /**
     * Given a list of ParseTrees, check with a sliding window if any sublist equals a given list of subtrees.
     */
    private fun containsSublistOfChildren(children: List<AbstractMultiTree<*>>, subtrees: List<AbstractMultiTree<*>>): Boolean {

        // We move a sliding window over the children of the given tree, and check for sublist equality.
        return 0.rangeTo(children.size - subtrees.size).any {
            children.subList(it, it + subtrees.size) == subtrees
        }
    }
}
