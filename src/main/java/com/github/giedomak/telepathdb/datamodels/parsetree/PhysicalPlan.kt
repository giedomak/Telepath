package com.github.giedomak.telepathdb.datamodels.parsetree

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.cardinalityestimation.CardinalityEstimation
import com.github.giedomak.telepathdb.datamodels.Edge
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore

class PhysicalPlan(
        isRoot: Boolean,
        override var operator: Int = 0
) : MultiTree(isRoot) {

    override val operatorName get() = SYMBOLIC_NAMES[operator]

    var memoryManagerId = -1L

    /**
     * Directly construct a non-root PhysicalPlan for the given [leaf].
     *
     * @param leaf The [Edge] which will be the leaf of this ParseTree.
     */
    constructor(leaf: Edge) : this(true) {
        this.operator = LOOKUP
        val child = PhysicalPlan(false)
        child.leaf = leaf
        this.children.add(child)
    }

    fun pathIdOfLookup(): Long {
        return PathIdentifierStore.getPathIdByEdges(children.map { it.leaf!! })
    }

    fun cardinality(): Long {
        return CardinalityEstimation.getCardinality(this)
    }

    fun cost(): Long {
        return TelepathDB.costModel.cost(this)
    }

    fun merge(tree: PhysicalPlan, operator: Int): PhysicalPlan {
        val root = PhysicalPlan(true, operator)
        root.children.add(this.clone())
        root.children.add(tree.clone())
        return root
    }

    companion object {

        const val LOOKUP = 1
        const val HASHJOIN = 2
        const val NESTEDLOOPJOIN = 3

        private val SYMBOLIC_NAMES = arrayOf(null, "LOOKUP", "HASHJOIN", "NESTEDLOOPJOIN")

    }
}