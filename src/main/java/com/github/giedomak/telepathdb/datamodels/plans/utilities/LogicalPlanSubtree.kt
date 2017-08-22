/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.plans.utilities

import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.utilities.LogicalPlanSubtree.subtreesOfSize

/**
 * Abstract the [subtreesOfSize] functionality into its own class.
 */
object LogicalPlanSubtree {

    /**
     * Get the size of this parsetree. This recurses through its children, we should augment our tree --> time is money.
     */
    fun getSize(tree: LogicalPlan): Int {

        // Switch case on operator
        return when (tree.operator) {

            LogicalPlan.LEAF -> 1
            LogicalPlan.KLEENE_STAR -> tree.children.sumBy { getSize(it) } + 1

            else -> tree.children.sumBy { getSize(it) }
        }
    }

    /**
     * Find all (partial) subtrees of a given [targetSize].
     *
     * We use a sliding window to try and find a subList of a nodes' children which does match the given [targetSize].
     *
     * Example tree:
     *
     *         CONCATENATION
     *        /  |     |  |  \
     *       a  UNION  e  f   g
     *          / | \
     *         b  c  d
     *
     * subtreesOfSize(tree, 2):
     *
     *     UNION   UNION    CONCATENATION    CONCATENATION
     *      / \     / \         /   \            /   \
     *     b   c   c   d       e     f          f     g
     *
     * @param targetSize We are looking for all (partial) subtrees of this size.
     */
    fun subtreesOfSize(tree: LogicalPlan, targetSize: Int): List<LogicalPlan> {

        // Break recursion if we are the targetSize
        if (getSize(tree) == targetSize) return listOf(tree)

        // Init our results list
        val subtrees = mutableListOf<LogicalPlan>()

        for ((index, child) in tree.children.withIndex()) {

            // Cache the size of this child
            var accumulatedSize = getSize(child)

            // If the size of this child is smaller than the targetSize, we'll try to concatenate with our
            // brothers and sisters. We'll traverse increasingly linearly.
            if (accumulatedSize < targetSize) {

                // Trying to find a subList of our children which together have the targetSize.
                for (toIndex in (index + 1)..(tree.children.size - 1)) {

                    // Add the size of our brother to the accumulatedSize
                    accumulatedSize += getSize(tree.children[toIndex])

                    // If we've jumped over our targetSize, we'll just try again with our brother as starting child.
                    if (accumulatedSize > targetSize) break

                    // Yay, we've found a subList which has our beloved targetSize.
                    if (accumulatedSize == targetSize) {
                        // Clone our tree since we are modifying it, and set the subList as its children.
                        val clone = tree.clone()
                        clone.children = clone.children.subList(index, toIndex + 1)
                        // Add to the results, and we're done for this window.
                        subtrees.add(clone)
                        break
                    }
                }
            } else {
                // So while searching for a subList, our starting child already exceeded the given targetSize.
                // Try to find some matches in that subtree.
                subtrees.addAll(subtreesOfSize(child, targetSize))
            }
        }
        return subtrees
    }
}
