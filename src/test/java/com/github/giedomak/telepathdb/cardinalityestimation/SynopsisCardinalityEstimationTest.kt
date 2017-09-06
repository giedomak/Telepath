package com.github.giedomak.telepathdb.cardinalityestimation

import com.github.giedomak.telepathdb.cardinalityestimation.synopsis.Synopsis
import com.github.giedomak.telepathdb.datamodels.graph.Edge
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.lessThan
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
            on { `in`(Edge("b")) }.doReturn(10)
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

    @Test
    fun cardinalityEstimationOf2Concatenations() {

        val synopsis = mockSynopsis()

        val cardinalityEstimation = SynopsisCardinalityEstimation(mock())
        cardinalityEstimation.synopsis = synopsis

        //             HASH_JOIN
        //             /       \
        //     INDEX_LOOKUP  INDEX_LOOKUP
        //          |            /  \
        //          0           1    2
        val child1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("0"))
        val child2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("1", "2"))
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.HASH_JOIN, listOf(child1, child2))

        val estimation = cardinalityEstimation.getCardinality(physicalPlan)
        val expected = 42093L
        val actual = 47982L

        val error = (actual - estimation) / actual.toDouble()

        assertEquals(expected, estimation)
        assertThat(error, lessThan(0.13))

    }

    @Test
    fun cardinalityEstimationOf3Concatenations() {

        val synopsis = mockSynopsis()

        val cardinalityEstimation = SynopsisCardinalityEstimation(mock())
        cardinalityEstimation.synopsis = synopsis

        //             HASH_JOIN
        //             /       \
        //     INDEX_LOOKUP  INDEX_LOOKUP
        //          |            /  |  \
        //          0           1   2   3
        val child1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("0"))
        val child2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("1", "2", "3"))
        val physicalPlan = PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.HASH_JOIN, listOf(child1, child2))

        val estimation = cardinalityEstimation.getCardinality(physicalPlan)
        val expected = 59126L
        val actual = 70006L

        val error = (actual - estimation) / actual.toDouble()

        assertEquals(expected, estimation)
        assertThat(error, lessThan(0.16))

    }

    private fun mockSynopsis(): Synopsis {
        // Mock the synopsis of src/test/resources/cite.txt
        return mock {
            // SYN1
            on { out(Edge("0")) }.doReturn(4679)
            on { `in`(Edge("0")) }.doReturn(7537)
            on { paths(Edge("0")) }.doReturn(16539)
            on { pairs(Edge("0")) }.doReturn(16539)

            on { out(Edge("1")) }.doReturn(4695)
            on { `in`(Edge("1")) }.doReturn(7538)
            on { paths(Edge("1")) }.doReturn(16693)
            on { pairs(Edge("1")) }.doReturn(16693)

            on { out(Edge("2")) }.doReturn(4741)
            on { `in`(Edge("2")) }.doReturn(7604)
            on { paths(Edge("2")) }.doReturn(16879)
            on { pairs(Edge("2")) }.doReturn(16879)

            on { out(Edge("3")) }.doReturn(4748)
            on { `in`(Edge("3")) }.doReturn(7488)
            on { paths(Edge("3")) }.doReturn(16641)
            on { pairs(Edge("3")) }.doReturn(16641)

            // SYN2
            on { out(Pair(Edge("0"), Edge("1"))) }.doReturn(3233)
            on { `in`(Pair(Edge("0"), Edge("1"))) }.doReturn(5309)
            on { middle(Pair(Edge("0"), Edge("1"))) }.doReturn(2839)
            on { paths(Pair(Edge("0"), Edge("1"))) }.doReturn(30016)
            on { pairs(Pair(Edge("0"), Edge("1"))) }.doReturn(29014)
            on { one(Pair(Edge("0"), Edge("1"))) }.doReturn(8139)
            on { two(Pair(Edge("0"), Edge("1"))) }.doReturn(10529)

            on { out(Pair(Edge("1"), Edge("2"))) }.doReturn(3193)
            on { `in`(Pair(Edge("1"), Edge("2"))) }.doReturn(5455)
            on { middle(Pair(Edge("1"), Edge("2"))) }.doReturn(2901)
            on { paths(Pair(Edge("1"), Edge("2"))) }.doReturn(31095)
            on { pairs(Pair(Edge("1"), Edge("2"))) }.doReturn(30077)
            on { one(Pair(Edge("1"), Edge("2"))) }.doReturn(8078)
            on { two(Pair(Edge("1"), Edge("2"))) }.doReturn(10936)

            on { out(Pair(Edge("2"), Edge("3"))) }.doReturn(3269)
            on { `in`(Pair(Edge("2"), Edge("3"))) }.doReturn(5273)
            on { middle(Pair(Edge("2"), Edge("3"))) }.doReturn(2886)
            on { paths(Pair(Edge("2"), Edge("3"))) }.doReturn(30788)
            on { pairs(Pair(Edge("2"), Edge("3"))) }.doReturn(29752)
            on { one(Pair(Edge("2"), Edge("3"))) }.doReturn(8211)
            on { two(Pair(Edge("2"), Edge("3"))) }.doReturn(10681)
        }
    }

}