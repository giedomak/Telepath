/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.parsetree

object ParseTreeContainment {

    /**
     * Given a root [ParseTree], check if [s1] and [s2] are contained as subtrees in [root] through the given [operatorId].
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
     * @param root The root ParseTree we'll be using to check containment of given subtrees.
     * @param s1 The first subtree.
     * @param s2 The second subtree.
     * @param operatorId The operatorId through which [s1] and [s2] are connected.
     * @return True if [s1] and [s2] are connected through [operatorId] and contained in [root].
     */
    fun containsSubtreesThroughOperator(root: ParseTree, s1: ParseTree, s2: ParseTree, operatorId: Int): Boolean {

        val subtrees = mutableListOf<ParseTree>()

        // If we are dealing with a subtree that is rooted with the same operator as the one we will be checking,
        // we have to only consider its children.
        for (subtree in listOf(s1, s2)) {
            if (subtree.operatorId == operatorId) subtrees.addAll(subtree.children) else subtrees.add(subtree)
        }

        // Check for each node which is equal to our operatorId, if our subtrees list is
        // directly contained in its children and its indices are next to each other.
        return root.postOrderTreeWalk()
                .filter { it.operatorId == operatorId }
                .anyMatch { containsSublistOfChildren(it, subtrees) }
    }

    /**
     * Given a ParseTree, check if a sliding window over its children equals a given list of subtrees.
     */
    private fun containsSublistOfChildren(tree: ParseTree, subtrees: List<ParseTree>): Boolean {

        // We move a sliding window over the children of the given tree, and check for sublist equality.
        return 0.rangeTo(tree.children.size - subtrees.size).any {
            tree.children.subList(it, it + subtrees.size) == subtrees
        }
    }
}
