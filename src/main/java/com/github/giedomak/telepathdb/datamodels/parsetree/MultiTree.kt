package com.github.giedomak.telepathdb.datamodels.parsetree

import com.github.giedomak.telepathdb.datamodels.Edge

abstract class MultiTree(
        var isRoot: Boolean = false,
        var leaf: Edge? = null
) : Cloneable {

    abstract var operator: Int
    var children = mutableListOf<MultiTree>()
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
    fun getChild(index: Int): MultiTree? {
        return children.getOrNull(index)
    }

    /**
     * Set a child at a given index in our [children] list.
     * We'll replace or add when needed.
     *
     * @param index Index of the child.
     * @param tree ParseTree we will be setting as our child.
     */
    fun setChild(index: Int, tree: MultiTree) {
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
    public override fun clone(): MultiTree {

        // make a clone of the current tree
        val clonedTree = super.clone() as MultiTree

        // recursively clone the children so that we don't keep references to the same objects
        clonedTree.children = this.children.mapTo(mutableListOf(), { it.clone() })

        return clonedTree
    }

    /**
     * Post-order treewalk.
     *
     * @return [Sequence] of ParseTrees
     */
    fun <T : MultiTree> postOrderTreeWalk(): Sequence<T> {
        return children.asSequence()
                .flatMap { it.postOrderTreeWalk<T>() }
                .plusElement(this as T)
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
    fun flatten(): MultiTree {
        return MultiTreeFlattener.flatten(this)
    }

    //
    // ---------------- EQUALS & HASHCODE & TO-STRING ----------------
    //

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MultiTree

        if (leaf != other.leaf) return false
        if (isRoot != other.isRoot) return false
        if (children != other.children) return false

        return true
    }

    abstract override fun hashCode(): Int

    override fun toString(): String {
        return this.javaClass.simpleName + "(operator=$operatorName, leaf=$leaf, isRoot=$isRoot, children=$children)"
    }

}