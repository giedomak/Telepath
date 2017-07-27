/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels

import com.telepathdb.datamodels.ParseTree.Companion
import com.telepathdb.datamodels.stores.PathIdentifierStore
import java.util.*
import java.util.stream.Stream

/**
 * Data-structure to represent the given user input, and physical plans.
 *
 * This data-structure is used for both representing the user input, and for physical plans.
 * It basically is a binary-tree. Meaning its children are in turn also ParseTrees.
 *
 * User input will get parsed through ANTLR into a parseTree by the [StaticParserRPQ][com.telepathdb.staticparser.StaticParserRPQ].
 *
 * The [Planner][com.telepathdb.planner.Planner] will parse such a parseTree from user input, into a physical plan.
 * These physical plans can have slightly different operators and payloads in regard to a parseTree generated from user input.
 *
 * @property id An ID given to a parseTree in order to make the life of the [MemoryManager][com.telepathdb.memorymanager.MemoryManager] easier.
 * @property operatorId An [Int] representing the operator. See our [Companion] for these constants.
 * @property leaf The payload when this parseTree is a leaf. Can also be set in the property [edge].
 * @property edge The payload when this parseTree is a leaf. Can also be set in the property [leaf].
 * @property isRoot Boolean value indicating if this parseTree is the root of the binary-tree.
 * @property children Ordered list with the children of this parseTree, which are also parseTrees.
 */
class ParseTree() : Cloneable {

    val id: Long
    var operatorId: Int = 0
    private var leaf: String? = null
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
     * Set the leaf and set the operatorId; we can either be a leaf OR an internal node.
     *
     * A leaf can either be stored as a [String] in the [leaf] property, or as an [Edge] in the [edge] property.
     *
     * @param leaf Payload of the leaf.
     */
    fun setLeaf(leaf: String) {
        this.operatorId = LEAF
        this.leaf = leaf
        this.edge = null
    }

    /**
     * Set the leaf and set the operatorId; we can either be a leaf OR an internal node.
     *
     * A leaf can either be stored as a [String] in the [leaf] property, or as an [Edge] in the [edge] property.
     *
     * @param edge Payload of the leaf
     */
    fun setLeaf(edge: Edge) {
        this.edge = edge
        this.operatorId = LEAF
        this.leaf = null
    }

    /**
     * Set the operatorId and reset the leaf; we can either be a leaf OR an internal node.
     *
     * @param operator The [Int] indicating our operator. Should be the index of [SYMBOLIC_NAMES].
     */
    fun setOperator(operator: Int) {
        this.operatorId = operator
        this.leaf = null
        this.edge = null
    }

    /**
     * Check if a child exists with [index] in our [children] list.
     *
     * @param index Index of the child we want to check.
     * @return Boolean value indicating if a child exists at the given [index].
     */
    fun hasChild(index: Int): Boolean {
        return getChild(index) != null
    }

    /**
     * Return the child at a given index or null when not existent.
     *
     * @param index Index of the child
     * @return Child at the given index from our [children] list.
     */
    fun getChild(index: Int): ParseTree? {
        if (children!!.size > index) {
            return children!![index]
        }
        return null
    }

    /**
     * Set a child at a given index in our [children] list.
     * We'll replace or add when needed.
     *
     * @param index Index of the child.
     * @param tree ParseTree we will be setting as our child.
     */
    fun setChild(index: Int, tree: ParseTree) {
        tree.isRoot = false
        try {
            this.children!![index] = tree
        } catch (e: IndexOutOfBoundsException) {
            this.children!!.add(index, tree)
        }
    }

    /**
     * Get the correct value of this node when it is a leaf or an internal node.
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
                // This will show something like `LOOKUP[2]` if we have 2 children.
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
     * Boolean value indicating if this parseTree is a leaf.
     *
     * @return Boolean value indicating if this node is a leaf.
     */
    val isLeaf: Boolean
        get() = leaf != null || edge != null

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

    /**
     * Recursive function calculating the depth of the parseTree rooted at ourselves.
     *
     * @return The depth of this parseTree.
     */
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

        /**
         * Creates a LOOKUP parseTree given a [pathId].
         *
         * This function creates a parseTree where the root has the operator [LOOKUP] and where
         * it has only one level of children. Each edge along the [pathId] will be a leaf child from this root.
         *
         * @param pathId The ID indicating a path from which we will get the list of edges.
         * @return A ParseTree with depth 2, where the root is the operator [LOOKUP] and its children are leafs with the edge labels from the [pathId].
         */
        fun createLookupTree(pathId: Long): ParseTree {

            // Get the list of edges from the PathIdentifierStore
            val edges = PathIdentifierStore.getEdgeSet(pathId)

            // Create the root of our new ParseTree
            val tree = ParseTree(true)
            tree.setOperator(LOOKUP)

            // Create a child leaf for each edge
            edges.stream()
                    .map {
                        val parseTree = ParseTree()
                        parseTree.setLeaf(it)
                        parseTree
                    }
                    .forEach { tree.children!!.add(it) }

            return tree
        }

        /**
         * Create a ParseTree concatenating two given parseTrees.
         *
         * @param p1 The to-be left child of our new parseTree.
         * @param p2 The to-be right child of our new parseTree.
         * @return A new parseTree with the [CONCATENATION] operator, and the given parseTrees set as child.
         */
        fun createConcatenationTree(p1: ParseTree, p2: ParseTree): ParseTree {

            // Create the root of our new ParseTree
            val tree = ParseTree(true)
            tree.setOperator(CONCATENATION)

            // Set the given params as the children
            tree.setChild(0, p1)
            tree.setChild(1, p2)

            return tree
        }
    }
}
