/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels

import com.telepathdb.datamodels.stores.PathIdentifierStore
import com.telepathdb.datamodels.utilities.ParseTreePrinter
import java.util.*
import java.util.stream.Stream

/**
 * Our internal representation of a query; how we can parse the user-input.
 */
class ParseTree() : Cloneable {

    val id: Long
    var operatorId: Int = 0 // Can be one of above constants if this node is an internal node
    private var leaf: String? = null // The payload when this node is a leaf
    var edge: Edge? = null
    var isRoot = false
    var children: MutableList<ParseTree>? = null

    init {
        this.children = ArrayList<ParseTree>()
        this.id = maxId++
    }

    constructor(isRoot: Boolean) : this() {
        this.isRoot = isRoot
    }

    /**
     * Set the leaf and reset the operatorId; we can either be a leaf OR an internal node
     */
    fun setLeaf(leaf: String) {
        this.operatorId = LEAF
        this.leaf = leaf
        this.edge = null
    }

    /**
     * Set the operatorId and reset the leaf; we can either be a leaf OR an internal node
     */
    fun setOperator(operator: Int) {
        this.operatorId = operator
        this.leaf = null
        this.edge = null
    }

    fun hasChild(index: Int): Boolean {
        return getChild(index) != null
    }

    fun getChild(index: Int): ParseTree? {
        if (children!!.size > index) {
            return children!![index]
        }
        return null
    }

    fun setChild(index: Int, tree: ParseTree) {
        tree.isRoot = false
        try {
            this.children!![index] = tree
        } catch (e: IndexOutOfBoundsException) {
            this.children!!.add(index, tree)
        }
    }

    /**
     * Get the correct value of this node when it is a leaf or a internal node.
     *
     * @return String with the value of the Leaf or the Symbolic name of the operatorId
     */
    val leafOrOperator: String
        get() {
            if (leaf != null) {
                return leaf!!
            } else if (edge != null) {
                return edge!!.label
            } else {
                return symbolicName + "[" + children!!.size + "]"
            }
        }

    /**
     * Convert the operatorId identifier back to its symbolic name.
     *
     * @return String with the Symbolic name of the operatorId
     */
    private val symbolicName: String?
        get() = SYMBOLIC_NAMES[operatorId]

    /**
     * @return Boolean value indicating if this node is a leaf
     */
    val isLeaf: Boolean
        get() = leaf != null || edge != null

    fun setLeaf(edge: Edge) {
        this.edge = edge
        this.operatorId = LEAF
        this.leaf = null
    }

    /**
     * Deep clone the current parsetree.
     *
     * @return A deep clone of the current parsetree.
     */
    public override fun clone(): ParseTree {

        var clonedTree: ParseTree? = null

        // make a clone of the current tree
        try {
            clonedTree = super.clone() as ParseTree
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }

        // reset our children
        clonedTree!!.children = ArrayList<ParseTree>()

        // recusively clone the left and right childs (parsetrees) so that we don't keep references to the same objects
        for (child in this.children!!) {
            clonedTree.children!!.add(child.clone())
        }

        return clonedTree
    }

    /**
     * Post-order treewalk
     *
     * @return Stream of ParseTrees
     */
    fun postOrderTreeWalk(): Stream<ParseTree> {
        return Stream.concat(
                children!!.stream()
                        .flatMap<ParseTree> { it.postOrderTreeWalk() }, Stream.of(this)
        )
    }

    /**
     * Check if the current tree contains a given operatorId.
     *
     * @param operator The operatorId constant (e.g. ParseTree.UNION) to check for containment in the tree.
     * @return Boolean indicating if the tree contains the operatorId.
     */
    fun containsOperator(operator: Int): Boolean {
        return postOrderTreeWalk().anyMatch { t -> t.operatorId == operator }
    }

    fun level(): Int {
        if (isLeaf)
            return 1

        val childLevels = ArrayList<Int>()
        children!!.forEach { childLevels.add(it.level()) }

        return Collections.max(childLevels) + 1
    }

    //
    // ---------------- EQUALS & HASHCODE & TO-STRING ----------------
    //

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParseTree

        if (operatorId != other.operatorId) return false
        // leafOrOperator will check for edge or leaf
        if (leafOrOperator != other.leafOrOperator) return false
        if (isRoot != other.isRoot) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operatorId
        result = 31 * result + leafOrOperator.hashCode()
        result = 31 * result + isRoot.hashCode()
        result = 31 * result + (children?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ParseTree(id=$id, operator=" + SYMBOLIC_NAMES[operatorId] + ", leaf=$leaf, edge=$edge, isRoot=$isRoot, children=$children)"
    }


    //
    // ---------------- COMPANION ----------------
    //

    companion object {

        // Our public constants identifying our symbolic names
        const val KLEENE_STAR = 1
        const val PLUS = 2
        const val CONCATENATION = 3
        const val UNION = 4
        const val LEAF = 5
        const val LOOKUP = 6

        private val SYMBOLIC_NAMES = arrayOf<String?>(null, "KLEENE_STAR", "PLUS", "CONCATENATION", "UNION", "LEAF", "LOOKUP")
        private var maxId: Long = 1

        //
        // ---------------- METHODS ----------------
        //

        fun createLookupTree(pathId: Long): ParseTree {

            val edges = PathIdentifierStore.getEdgeSet(pathId)

            val tree = ParseTree(true)

            tree.setOperator(LOOKUP)

            edges.stream()
                    .map {
                        val parseTree = ParseTree()
                        parseTree.setLeaf(it)
                        parseTree
                    }
                    .forEach { tree.children!!.add(it) }

            return tree
        }

        fun createConcatenationTree(p1: ParseTree, p2: ParseTree): ParseTree {

            val tree = ParseTree(true)

            tree.setOperator(CONCATENATION)

            tree.setChild(0, p1)
            tree.setChild(1, p2)

            return tree
        }
    }
}
