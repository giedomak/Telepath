package com.github.giedomak.telepathdb.planner.enumerator

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.Query
import com.github.giedomak.telepathdb.datamodels.plans.LogicalPlan
import com.github.giedomak.telepathdb.datamodels.plans.PhysicalPlanTest
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.github.giedomak.telepathdb.physicaloperators.PhysicalOperator
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import kotlin.test.assertEquals

class SimpleEnumeratorTest {

    // Mock the path index to return the expected results.
    private val index = KPathIndexInMemory()

    // Mock our module to return the store and the index.
    private val telepathdb = mock<TelepathDB> {
        on { kPathIndex }.doReturn(index)
    }

    // Let our query know the module we are using.
    private val query = mock<Query> {
        on { telepathDB }.doReturn(telepathdb)
    }

    @Test
    fun enumeratesConcatenation() {

        // Mock a k-value which is smaller than our concatenated lookup
        index.k = 3

        // Given these two trees and the CONCATENATION operator:
        //
        //      INDEX_LOOKUP       INDEX_LOOKUP
        //        /     \            /     \
        //       a      b           c      d
        val tree1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), query)
        val tree2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("c", "d"), query)

        // Expected:
        //
        //                HASH_JOIN                NESTED_LOOP_JOIN
        //                 /    \                     /       \
        //       INDEX_LOOKUP INDEX_LOOKUP    INDEX_LOOKUP INDEX_LOOKUP
        //         /     \      /    \          /     \      /    \
        //        a      b     c     d         a      b     c     d
        val expected = listOf(
                PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.HASH_JOIN, listOf(tree1, tree2)),
                PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.NESTED_LOOP_JOIN, listOf(tree1, tree2))
        )

        val actual = SimpleEnumerator.enumerate(tree1, tree2, LogicalPlan.CONCATENATION).toList()

        assertEquals(expected, actual)
    }

    @Test
    fun enumeratesConcatenationWithIndexLookup() {

        // Mock a k-value which is big enough for our combined lookup.
        index.k = 4

        // Given these two trees and the CONCATENATION operator:
        //
        //      INDEX_LOOKUP       INDEX_LOOKUP
        //        /     \            /     \
        //       a      b           c      d
        val tree1 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b"), query)
        val tree2 = PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("c", "d"), query)

        // Expected:
        //
        //       INDEX_LOOKUP              HASH_JOIN                NESTED_LOOP_JOIN
        //        / |  |  \                  /    \                     /       \
        //       a  b  c   d       INDEX_LOOKUP INDEX_LOOKUP    INDEX_LOOKUP INDEX_LOOKUP
        //                           /     \      /    \          /     \      /    \
        //                          a      b     c     d         a      b     c     d
        val expected = listOf(
                PhysicalPlanTest.generatePhysicalPlan(PhysicalOperator.INDEX_LOOKUP, listOf("a", "b", "c", "d")),
                PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.HASH_JOIN, listOf(tree1, tree2)),
                PhysicalPlanTest.generatePhysicalPlanWithChildren(PhysicalOperator.NESTED_LOOP_JOIN, listOf(tree1, tree2))
        )

        val actual = SimpleEnumerator.enumerate(tree1, tree2, LogicalPlan.CONCATENATION).toList()

        assertEquals(expected, actual)
    }

}