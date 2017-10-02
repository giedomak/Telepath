package com.github.giedomak.telepathdb.planner

import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlan
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import java.util.*

object NaivePlanner : Planner {

    private val random = Random()

    override fun generate(logicalPlan: LogicalPlan): PhysicalPlan {

        if (logicalPlan.height() > 1 || logicalPlan.operator != LogicalPlan.CONCATENATION) {
            throw IllegalArgumentException("NaivePlanner is only for concatenations")
        }

        val leafs = logicalPlan.children.map { it.leaf!! }

        if (leafs.size == 3) {

            if (logicalPlan.query.telepathDB.kPathIndex.k == 2) {

                val child1 = PhysicalPlan(logicalPlan.query, PhysicalOperator.INDEX_LOOKUP)
                child1.children.add(PhysicalPlan(logicalPlan.query, leafs[0]))
                child1.children.add(PhysicalPlan(logicalPlan.query, leafs[1]))

                val child2 = PhysicalPlan(logicalPlan.query, leafs[2])

                val root = PhysicalPlan(logicalPlan.query, PhysicalOperator.HASH_JOIN)

                if (random.nextBoolean()) {
                    root.children.add(child1.flatten())
                    root.children.add(child2)
                } else {
                    root.children.add(child2)
                    root.children.add(child1.flatten())
                }

                return root

            } else if (logicalPlan.query.telepathDB.kPathIndex.k == 1) {

                val root = PhysicalPlan(logicalPlan.query, PhysicalOperator.HASH_JOIN)

                if (random.nextBoolean()) {

                    val child1 = PhysicalPlan(logicalPlan.query, PhysicalOperator.HASH_JOIN)
                    child1.children.add(PhysicalPlan(logicalPlan.query, leafs[0]))
                    child1.children.add(PhysicalPlan(logicalPlan.query, leafs[1]))

                    val child2 = PhysicalPlan(logicalPlan.query, leafs[2])

                    root.children.add(child1)
                    root.children.add(child2)
                } else {

                    val child2 = PhysicalPlan(logicalPlan.query, PhysicalOperator.HASH_JOIN)
                    child2.children.add(PhysicalPlan(logicalPlan.query, leafs[1]))
                    child2.children.add(PhysicalPlan(logicalPlan.query, leafs[2]))

                    val child1 = PhysicalPlan(logicalPlan.query, leafs[0])

                    root.children.add(child1)
                    root.children.add(child2)
                }

                return root
            } else {
                throw IllegalArgumentException("NaivePlanner is only for k = 1 or k = 2")
            }

        } else {
            throw IllegalArgumentException("NaivePlanner is only for concatenations of size 3")
        }

    }

}