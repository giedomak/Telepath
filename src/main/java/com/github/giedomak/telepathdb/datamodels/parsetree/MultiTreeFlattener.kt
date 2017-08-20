/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http: *www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.parsetree

/**
 * Flatten ParseTrees into multi-trees.
 *
 * We escalate the children from a node to its parent if both the node and its parent have the same operator.
 *
 * Example input:
 *
 *            CONCATENATION
 *          /       |        \
 *         a  CONCATENATION   f
 *              /      \
 *          UNION       e
 *         /    \
 *        b    UNION
 *             /  \
 *            c    d
 *
 * Output:
 *
 *        CONCATENATION
 *        /   |    |  \
 *       a  UNION  e   f
 *          / | \
 *         b  c  d
 */
object MultiTreeFlattener {

    /**
     * Flatten a given ParseTree recursively into a multi-tree.
     *
     * @param tree The given input ParseTree.
     * @return A flattened ParseTree from the given input.
     */
    fun <ImplementingTree : MultiTree<ImplementingTree>> flatten(tree: ImplementingTree): ImplementingTree {

        // Break recursion once we've found a leaf.
        if (tree.isLeaf) return tree

        // Since we could be escalating multiple children from the same parent, we have to track these escalations.
        var offset = 0

        for (index in tree.children.indices) {

            // Flatten each child recursively
            val child = tree.children[index + offset]
            val flattenedChild = flatten(child)

            // The subtree rooted at flattenedChild, should all be flattened now.
            // So escalate its children if the operators match.
            if (child.operator == tree.operator) {

                tree.children.removeAt(index + offset)
                tree.children.addAll(index + offset, flattenedChild.children)

                offset += flattenedChild.children.size - 1
            }
        }

        // We're done
        return tree
    }
}
