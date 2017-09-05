package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.cardinalityestimation.synopsis.Synopsis
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import org.junit.Test
import kotlin.test.assertEquals

class SynopsisCardinalityEstimationTest {

    @Test
    fun returnsTheCardinalityOfSimpleJoins() {

        // Should only use the pairs synopsis from SYN2.
        val synopsis = mock<Synopsis> {
            on { pairs(Pair(Edge("a"), Edge("b"))) }.doReturn(400)
        }

        val cardinalityEstimation = spy(SynopsisCardinalityEstimation(mock()))
        cardinalityEstimation.synopsis = synopsis

        //             HASH_JOIN
        //             /       \
        //     INDEX_LOOKUP  INDEX_LOOKUP
        //          |             |
        //          a             b
        val child1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a"))
        val child2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("b"))

        PhysicalOperator.JOIN_OPERATORS.forEach {

            val physicalPlan = PhysicalPlanTest.generatePhysicalPlanWithChildren(it, listOf(child1, child2))

            val actual = cardinalityEstimation.getCardinality(physicalPlan)

            assertEquals(400L, actual)
        }
    }

    @Test
    fun returnsTheCardinalityOfAdvancedJoins() {

        // | T r/l1/l2 | = | T r/l1 | * ( l1/l2.two / l1.in )
        val synopsis = mock<Synopsis> {
            on { pairs(Pair(Edge("a"), Edge("b"))) }.doReturn(400)
            on { two(Pair(Edge("b"), Edge("c"))) }.doReturn(1)
            on { `in`(Edge("b"))}.doReturn(10)
        }

        val cardinalityEstimation = spy(SynopsisCardinalityEstimation(mock()))
        cardinalityEstimation.synopsis = synopsis

        //             HASH_JOIN
        //             /       \
        //     INDEX_LOOKUP  INDEX_LOOKUP
        //          |            /  \
        //          a           b    c
        val child1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a"))
        val child2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("b", "c"))

        PhysicalOperator.JOIN_OPERATORS.forEach {

            val physicalPlan = PhysicalPlanTest.generatePhysicalPlanWithChildren(it, listOf(child1, child2))

            val actual = cardinalityEstimation.getCardinality(physicalPlan)

            assertEquals(40L, actual)
        }
    }

}