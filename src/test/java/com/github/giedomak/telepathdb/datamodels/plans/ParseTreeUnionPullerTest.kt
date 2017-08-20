package com.github.giedomak.telepathdb.datamodels.plans

class ParseTreeUnionPullerTest {

//    @Test
//    fun pullsOutUnionsIntoMultipleParseTrees() {
//
//        // Given
//        val input = exampleUnionParseTree()
//        val actual = ParseTreeUnionPuller.parse(input)
//
//        // Create expected parseTree
//        //       CONCATENATION
//        //        /      \
//        //  CONCATENATION  d
//        //      /   \
//        //     a     b
//        val child1 = LogicalPlanTest.generateLogicalPlan(
//                LogicalPlan.CONCATENATION, listOf("a", "b"))
//        val root1 = LogicalPlanTest.generateLogicalPlan(
//                LogicalPlan.CONCATENATION, listOf("d"))
//        root1.children.add(0, child1)
//
//        // Create expected parseTree
//        //       CONCATENATION
//        //        /      \
//        //  CONCATENATION  d
//        //      /   \
//        //     a     c
//        val child2 = LogicalPlanTest.generateLogicalPlan(
//                LogicalPlan.CONCATENATION, listOf("a", "c"))
//        val root2 = LogicalPlanTest.generateLogicalPlan(
//                LogicalPlan.CONCATENATION, listOf("d"))
//        root2.children.add(0, child2)
//
//        assertEquals(listOf(root1, root2), actual)
//    }
//
//    @Test
//    fun splitsParseTreesWhenRootIsUnion() {
//
//        // Given:
//        //     UNION
//        //      / \
//        //     a   b
//        val input = LogicalPlanTest.generateLogicalPlan(LogicalPlan.UNION, listOf("a", "b"))
//        val actual = ParseTreeUnionPuller.parse(input)
//
//        // Generate expected
//        val a = LogicalPlanTest.createSimpleLogicalPlan("a")
//        val b = LogicalPlanTest.createSimpleLogicalPlan("b")
//
//        assertEquals(listOf(a, b), actual)
//    }

    private fun exampleUnionParseTree(): LogicalPlan {

        // Your input: a/(b|c)/d
        //
        //        CONCATENATION[2]
        //       / \
        //      /   \
        //     /     \
        //    /       \
        //    CONCATENATION[2]       d
        //   / \
        //  /   \
        //  a   UNION[2]
        //       / \
        //       b c
        val child2 = LogicalPlanTest.generateLogicalPlan(
                LogicalPlan.UNION, listOf("b", "c"))
        val child1 = LogicalPlanTest.generateLogicalPlan(
                LogicalPlan.CONCATENATION, listOf("a"))
        child1.setChild(1, child2)

        val root = LogicalPlanTest.generateLogicalPlan(LogicalPlan.CONCATENATION, listOf("d"))
        root.children.add(0, child1)

        return root
    }
}
