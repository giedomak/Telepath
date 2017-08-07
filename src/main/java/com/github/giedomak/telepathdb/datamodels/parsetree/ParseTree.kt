/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.parsetree

import com.github.giedomak.telepathdb.datamodels.Edge
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree.Companion
import com.github.giedomak.telepathdb.datamodels.parsetree.ParseTree.Companion.CONCATENATION
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
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
 * @property isRoot Boolean value indicating if this parseTree is the root of the binary-tree.
 * @property children Ordered list with the children of this parseTree, which are also parseTrees.
 * @property operatorId An [Int] representing the operator. See our [Companion] for these constants, i.e. [CONCATENATION].
 * @property isLeaf Boolean value indicating if this parseTree is a leaf.
 * @property leafOrOperator Get a String representing the leaf or operator. I.e. `a` or `CONCATENATION[3]`.
 * @property leaf The payload when this parseTree is a leaf, given as an [Edge].
 * @constructor Create a non-root empty ParseTree.
 */
class ParseTree() : Cloneable {

    val id: Long
    var isRoot = false
    var children = mutableListOf<ParseTree>()
    var operatorId: Int = 0
    val isLeaf get() = (operatorId == LEAF)
    val leafOrOperator get() = (leaf?.label ?: symbolicName + "[" + children.size + "]")
    // Convert the operatorId identifier back to its symbolic name.
    private val symbolicName get() = SYMBOLIC_NAMES[operatorId]

    // Make sure we set the operatorId to LEAF if we set a (correct) value.
    var leaf: Edge? = null
        set(value) {
            if (value != null) operatorId = LEAF
            field = value
        }

    init {
        this.id = maxId++
    }

    /**
     * Construct a ParseTree with the [isRoot] property set to the given param.
     *
     * @param isRoot Boolean value indicating if this new ParseTree should be a root node or not.
     */
    constructor(isRoot: Boolean) : this() {
        this.isRoot = isRoot
    }

    /**
     * Directly construct a non-root ParseTree for the given [leaf].
     *
     * @param leaf The [Edge] which will be the leaf of this ParseTree.
     */
    constructor(leaf: Edge) : this() {
        this.leaf = leaf
    }

    /**
     * Set the leaf by giving in a [String] which we'll convert to an [Edge].
     *
     * @param label The String for which we create an [Edge] and set it as leaf in this ParseTree.
     */
    fun setLeaf(label: String) {
        this.leaf = Edge(label)
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
        return children.getOrNull(index)
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
        if (hasChild(index)) {
            this.children[index] = tree
        } else {
            this.children.add(index, tree)
        }
    }

    /**
     * Deep clone the current parsetree.
     *
     * @return A deep clone of the current parsetree.
     */
    public override fun clone(): ParseTree {

        // make a clone of the current tree
        val clonedTree = super.clone() as ParseTree

        // recursively clone the children so that we don't keep references to the same objects
        clonedTree.children = this.children.mapTo(mutableListOf(), { it.clone() })

        return clonedTree
    }

    /**
     * Post-order treewalk.
     *
     * @return Stream of ParseTrees
     */
    fun postOrderTreeWalk(): Stream<ParseTree> {
        return Stream.concat(
                children.stream()
                        .flatMap<ParseTree> { it.postOrderTreeWalk() }, Stream.of(this)
        )
    }

    /**
     * Check if the current tree contains a given operatorId.
     *
     * @param operatorId The operatorId constant (e.g. ParseTree.UNION) to check for containment in the tree.
     * @return Boolean indicating if the tree contains the operatorId.
     */
    fun contains(operatorId: Int): Boolean {
        return postOrderTreeWalk().anyMatch { it.operatorId == operatorId }
    }

    /**
     * Recursive function calculating the depth of the parseTree rooted at ourselves.
     *
     * @return The depth of this parseTree.
     */
    fun level(): Int {
        if (isLeaf) return 1
        return children.map { it.level() }.max()!! + 1
    }

    /**
     * Delegate parse-tree-printing to our [ParseTreePrinter].
     */
    fun print() {
        ParseTreePrinter.printParseTree(this)
    }

    /**
     * Delegate parse-tree-flattening to our [ParseTreeFlattener].
     */
    fun flatten() {
        ParseTreeFlattener.flatten(this)
    }

    /**
     * Delegate parse-tree-union-pulling to our [ParseTreeUnionPuller].
     */
    fun pullUnions(): List<ParseTree> {
        return ParseTreeUnionPuller.parse(this)
    }

    /**
     * Delegate parse-tree-sizing to our [ParseTreeSizes].
     */
    fun subtreesOfSize(targetSize: Int): List<ParseTree> {
        return ParseTreeSizes.subtreesOfSize(this, targetSize)
    }

    //
    // ---------------- EQUALS & HASHCODE & TO-STRING ----------------
    //

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParseTree

        if (operatorId != other.operatorId) return false
        if (leaf != other.leaf) return false
        if (isRoot != other.isRoot) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operatorId
        result = 31 * result + (leaf?.hashCode() ?: 0)
        result = 31 * result + isRoot.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }

    override fun toString(): String {
        return "ParseTree(id=$id, operator=$symbolicName, leaf=$leaf, isRoot=$isRoot, children=$children)"
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

        private val SYMBOLIC_NAMES = arrayOf(null, "KLEENE_STAR", "PLUS", "CONCATENATION", "UNION", "LEAF", "LOOKUP")
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
            tree.operatorId = LOOKUP

            // Create a child leaf for each edge
            edges.map { ParseTree(it) }.forEach { tree.children.add(it) }

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
            tree.operatorId = CONCATENATION

            // Set the given params as the children
            tree.setChild(0, p1)
            tree.setChild(1, p2)

            return tree
        }
    }
}
