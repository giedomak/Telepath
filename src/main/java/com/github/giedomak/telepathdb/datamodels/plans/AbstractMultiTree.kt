package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.plans.utilities.MultiTreeFlattener
import com.github.giedomak.telepathdb.datamodels.plans.utilities.MultiTreePrinter

abstract class AbstractMultiTree<ImplementingTree : AbstractMultiTree<ImplementingTree>>(
        val query: Query,
        var leaf: Edge? = null
) : Cloneable {

    // Since we are a tree, our children are also of the type ImplementingTree.
    var children = mutableListOf<ImplementingTree>()

    // The ImplementingTree should implement the operator and its String representation.
    abstract var operator: Int
    // String representation of the operator.
    abstract val operatorName: String?

    // String representation of this Node, either the label if we are a leaf, or the operatorName if we are an operator.
    val nodeRepresentation get() = (leaf?.label ?: operatorName + "[" + children.size + "]")

    val isLeaf get() = (leaf != null)

    /**
     * Set the leaf by giving in a [String] which we'll convert to an [Edge].
     *
     * @param label The String for which we create an [Edge] and set it as leaf in this LogicalPlan.
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
    fun getChild(index: Int): ImplementingTree? {
        return children.getOrNull(index)
    }

    /**
     * Set a child at a given index in our [children] list.
     * We'll replace or add when needed.
     *
     * @param index Index of the child.
     * @param tree LogicalPlan we will be setting as our child.
     */
    fun setChild(index: Int, tree: ImplementingTree) {
        if (hasChild(index)) {
            this.children[index] = tree
        } else {
            this.children.add(index, tree)
        }
    }

    /**
     * Deep clone; also clone its children recusively.
     *
     * @return A deep clone of the calling [ImplementingTree].
     */
    public override fun clone(): ImplementingTree {

        // Make a clone of the current tree; yes yes, we can cast here.
        @Suppress("UNCHECKED_CAST")
        val clonedTree = super.clone() as ImplementingTree

        // Recursively clone the children so that we don't keep references to the same objects
        clonedTree.children = this.children.mapTo(mutableListOf(), { it.clone() })

        return clonedTree
    }

    /**
     * Post-order traversal.
     *
     * We expect the following tree to report 2, 5, 6, 3, 4, 1.
     *
     *          1
     *        / | \
     *       2  3  4
     *         / \
     *        5   6
     *
     * @return [Sequence] of [ImplementingTree] in post-order sequence.
     */
    fun postOrderTraversal(): Sequence<ImplementingTree> {
        @Suppress("UNCHECKED_CAST")
        return children.asSequence()
                .flatMap { it.postOrderTraversal() }
                .plusElement(this as ImplementingTree)
    }

    /**
     * Recursive function calculating the height of this tree.
     *
     * The height of a node is the number of edges on the longest path from the node to a leaf.
     * A leaf node will have a height of 0. The height of a tree would be the height of its root node.
     *
     * Example: Each node displays the height of the subtree rooted at that node.
     *
     *        3
     *       / \
     *      1   2
     *     / \   \
     *    0  0   1
     *          /
     *         0
     *
     * @return The height of this tree.
     */
    fun height(): Int {
        if (isLeaf) return 0
        return children.map { it.height() }.max()!! + 1
    }

    /**
     * Delegate parse-tree-printing to our [MultiTreePrinter].
     */
    fun print() {
        MultiTreePrinter.printMultiTree(this)
    }

    /**
     * Delegate parse-tree-flattening to our [MultiTreeFlattener].
     */
    fun flatten(): ImplementingTree {
        @Suppress("UNCHECKED_CAST")
        return MultiTreeFlattener.flatten(this as ImplementingTree)
    }

    //
    // ---------------- EQUALS & HASHCODE & TO-STRING ----------------
    //

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AbstractMultiTree<*>

        if (leaf != other.leaf) return false
        if (operator != other.operator) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operator
        result = 31 * result + (leaf?.hashCode() ?: 0)
        result = 31 * result + children.hashCode()
        return result
    }

    override fun toString(): String {
        return this.javaClass.simpleName + "(operator=$operatorName, leaf=$leaf, children=$children)"
    }

}