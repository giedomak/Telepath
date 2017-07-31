/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.utilities

import com.telepathdb.datamodels.ParseTree
import java.util.*

/**
 * Class to print [parse-trees][ParseTree].
 *
 * Example: your input: `a/(b+|c)|d`
 *
 * <pre>
 *             UNION
 *              / \
 *             /   \
 *            /     \
 *           /       \
 *          /         \
 *         /           \
 *        /             \
 *       /               \
 * CONCATENATION          d
 *      / \
 *     /   \
 *    /     \
 *   /       \
 *  a       UNION
 *           / \
 *          /   \
 *        PLUS   c
 *         /
 *        b
 * </pre>
 */
object ParseTreePrinter {

    /**
     * Static method we should call with the root parseTree as parameter in order to print it.
     *
     * @param root The root of the ParseTree we want to print
     */
    fun printParseTree(root: ParseTree) {
        val maxLevel = ParseTreePrinter.maxLevel(root)

        Logger.debug("", false)
        printNodeInternal(listOf(root), 1, maxLevel)
        Logger.debug("", true)

    }

    private fun printNodeInternal(nodes: List<ParseTree?>, level: Int, maxLevel: Int) {
        if (nodes.isEmpty() || ParseTreePrinter.isAllElementsNull(nodes))
            return

        val floor = maxLevel - level
        val endgeLines = Math.pow(2.0, Math.max(floor - 1, 0).toDouble()).toInt()
        val firstSpaces = Math.pow(2.0, floor.toDouble()).toInt() - 1
        val betweenSpaces = Math.pow(2.0, (floor + 1).toDouble()).toInt() - 1

        ParseTreePrinter.printWhitespaces(firstSpaces)

        val newNodes = ArrayList<ParseTree?>()
        for (node in nodes) {
            if (node != null) {
                print(node.leafOrOperator)
                newNodes.add(node.getChild(0))
                newNodes.add(node.getChild(1))
                newNodes.add(node.getChild(2))
            } else {
                newNodes.add(null)
                newNodes.add(null)
                print(" ")
            }

            ParseTreePrinter.printWhitespaces(betweenSpaces)
        }

        println("")
        Logger.debug("", false)

        for (i in 1..endgeLines) {
            for (j in nodes.indices) {
                ParseTreePrinter.printWhitespaces(firstSpaces - i)
                if (nodes[j] == null) {
                    ParseTreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1)
                    continue
                }

                if (nodes[j]!!.hasChild(0))
                    print("/")
                else
                    ParseTreePrinter.printWhitespaces(1)

                ParseTreePrinter.printWhitespaces(i + i - 1)

                if (nodes[j]!!.hasChild(1))
                    print("\\")
                else
                    ParseTreePrinter.printWhitespaces(1)

                ParseTreePrinter.printWhitespaces(i + i - 1)

                if (nodes[j]!!.hasChild(2))
                    print("\\")
                else
                    ParseTreePrinter.printWhitespaces(1)

                ParseTreePrinter.printWhitespaces(endgeLines + endgeLines - i)
            }

            println("")
            Logger.debug("", false)
        }

        printNodeInternal(newNodes, level + 1, maxLevel)
    }

    private fun printWhitespaces(count: Int) {
        for (i in 0..count - 1)
            print(" ")
    }

    private fun maxLevel(node: ParseTree): Int {
        if (node.isLeaf)
            return 1

        val childLevels = node.children.map { ParseTreePrinter.maxLevel(it) }

        return Collections.max(childLevels) + 1
    }

    private fun <T> isAllElementsNull(list: List<T>): Boolean {
        for (el in list) {
            if (el != null)
                return false
        }

        return true
    }
}
