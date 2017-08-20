package com.github.giedomak.telepathdb.datamodels.parsetree

import com.github.giedomak.telepathdb.datamodels.Edge
import com.github.giedomak.telepathdb.datamodels.Query

abstract class MultiTree<ImplementingTree : MultiTree<ImplementingTree>>(
        val query: Query,
        var leaf: Edge? = null
) : Cloneable {

    abstract var operator: Int
    var children = mutableListOf<ImplementingTree>()
    val nodeRepresentation get() = (leaf?.label ?: operatorName + "[" + children.size + "]")

    // Convert the operator identifier back to its symbolic name.
    abstract val operatorName: String?

    val isLeaf get() = (leaf != null)

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
    fun getChild(index: Int): ImplementingTree? {
        return children.getOrNull(index)
    }

    /**
     * Set a child at a given index in our [children] list.
     * We'll replace or add when needed.
     *
     * @param index Index of the child.
     * @param tree ParseTree we will be setting as our child.
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
     * Recursive function calculating the depth of the parseTree rooted at ourselves.
     *
     * @return The depth of this parseTree.
     */
    fun level(): Int {
        if (isLeaf) return 1
        return children.map { it.level() }.max()!! + 1
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

        other as MultiTree<*>

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