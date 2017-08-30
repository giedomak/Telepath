package com.github.giedomak.telepathdb.datamodels.plans

import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.memorymanager.SimpleMemoryManager
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator

/**
 * Representation of the physical plan.
 *
 * This class extends AbstractMultiTree and and has physical operators instread of logical operators in comparison
 * to the [LogicalPlan] class.
 *
 * @property query We always need a reference to our query which holds all the module implementations we'll need.
 * @property operator An [Int] representing the physical operator. See [HASH_JOIN] for an example.
 * @property operatorName This property gets the symbolic name [String] belonging to our operator, e.g. HASH_JOIN.
 * @property memoryManagerId This property will hold a reference to the [SimpleMemoryManager] slot we might be given
 *                           to indicate the location of intermediate results.
 */
class PhysicalPlan(
        query: Query,
        override var operator: Int = PhysicalOperator.LEAF
) : AbstractMultiTree<PhysicalPlan>(query) {

    val physicalOperator = PhysicalOperator.getPhysicalOperator(this)
    override val operatorName get() = physicalOperator?.javaClass?.simpleName

    /**
     * Directly construct a PhysicalPlan for the given [leaf].
     *
     * @param query We always need a reference to a query.
     * @param leaf The [Edge] which will be the leaf of this LogicalPlan.
     */
    constructor(query: Query, leaf: Edge) : this(query, PhysicalOperator.INDEX_LOOKUP) {
        // Create and set the child-leaf as a child of our INDEX_LOOKUP
        val child = PhysicalPlan(query)
        child.leaf = leaf
        this.children.add(child)
    }

    fun pathIdOfChildren(): Long {
        return query.telepathDB.pathIdentifierStore.getPathIdByEdges(children.map { it.leaf!! })
    }

    fun cardinality(): Long {
        return query.telepathDB.cardinalityEstimation.getCardinality(this)
    }

    fun cost(): Long {
        return query.telepathDB.costModel.cost(this)
    }

    fun merge(tree: PhysicalPlan, operator: Int): PhysicalPlan {
        val root = PhysicalPlan(query, operator)
        root.children.add(this.clone())
        root.children.add(tree.clone())
        return root
    }
}