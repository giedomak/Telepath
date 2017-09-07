# Planner

This document describes how the planner calculates the cheapest physical plan for a given logical plan.

1. __Flatten into multi-children tree__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.datamodels.plans.utilities/-multi-tree-flattener/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/datamodels/plans/utilities/MultiTreeFlattenerTest.kt#L15) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/datamodels/plans/utilities/MultiTreeFlattener.kt#L37)

  Logical plans are flattened to prepare them for the subtree generator.

  Given:

              CONCATENATION
                  /    \
                 a    CONCATENATION
                         /    \
                        b      c

  Output:

             CONCATENATION
                /  |  \
               a   b   c

2. __Generate subtrees of a given size__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.datamodels.plans.utilities/-logical-plan-subtree/index.html)  [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/datamodels/plans/utilities/LogicalPlanSubtreeTest.kt#L15) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/datamodels/plans/utilities/LogicalPlanSubtree.kt#L16)

  Let's say we are trying to calculate the cheapest physical plan for a plan with size `2`. Then we are generating all subtrees of size `1`, and check if we can combine them. These smaller subtrees have its cheapest physical plan already calculated, so we'll want to re-use those.

  Given:

                 CONCATENATION
                /  |     |  |  \
               a  UNION  e  f   g
                  / | \
                 b  c  d

  Subtrees of size `2`:

             UNION   UNION    CONCATENATION    CONCATENATION
              / \     / \         /   \            /   \
             b   c   c   d       e     f          f     g

3. __Check containment of subtrees__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.datamodels.plans.utilities/-multi-tree-containment/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/datamodels/plans/utilities/MultiTreeContainmentTest.kt#L19) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/datamodels/plans/utilities/MultiTreeContainment.kt#L13)

  When two subtrees are contained in the logical plan through any operator, we calculate the cheapest physical plan for the combination of those two subtrees concatenated by the operator.

  Given this logical plan:

                     UNION
                     /   \
         CONCATENATION   CONCATENATION
             /   \          /     \
            a     b        c       d

  Given `subtree1` and `subtree2`:

         CONCATENATION           CONCATENATION
             /    \                  /    \
            a      b                c      d

  `subtree1` and `subtree2` are contained in the logical plan through the `UNION` operator.

  _Second example:_

  Given this logical plan:

                 CONCATENATION
                 /  |  |  |  \
                a   b  c  d   e

  Given `subtree1` and `subtree2`:

            CONCATENATION              CONCATENATION
               /    \                     /    \
              b      c                   d      e

  `subtree1` and `subtree2` are contained in the logical plan through the `CONCATENATION` operator.

4. __Enumerate operators__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.planner.enumerator/-simple-enumerator/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/planner/enumerator/SimpleEnumeratorTest.kt) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/planner/enumerator/SimpleEnumerator.kt#L10)

  When two subtrees are contained through an operator in the logical plan, we'll calculate the cheapest physical plan for their combination. Remember we already know the cheapest physical plans for both subtrees.

  As an example, let's say we've got two subtrees contained through the `CONCATENATION` operator. We enumerate the logical operator into hash-join, nested-loop-join and index-lookup.

  Given these two trees and the CONCATENATION operator with a k-value greater than or equal to `4`:

              INDEX_LOOKUP       INDEX_LOOKUP
                /     \            /     \
               a      b           c      d

  Expected:

           INDEX_LOOKUP              HASH_JOIN                NESTED_LOOP_JOIN
            / |  |  \                  /    \                     /       \
           a  b  c   d       INDEX_LOOKUP INDEX_LOOKUP    INDEX_LOOKUP INDEX_LOOKUP
                               /     \      /    \          /     \      /    \
                              a      b     c     d         a      b     c     d

5. __Cardinality estimation__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.cardinalityestimation/-synopsis-cardinality-estimation/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/cardinalityestimation/SynopsisCardinalityEstimationTest.kt#L15) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/cardinalityestimation/SynopsisCardinalityEstimation.kt#L14)

  When we are dealing with intermediate results, we need the estimated cardinality of these intermediate results to calculate the cost.

  If we are dealing with only concatenations, we can use our SynopsisCardinalityEstimation. This cardinality estimator will track graph statistics into a synopsis. It will hold information on all concatenations up to `k = 2`.

  Using this synopsis we can estimate the cardinality of paths where `k = 3`, by using the synopsis for `k = 1` and `k = 2`.

  ```kotlin
    // See if we got one of these after flattening:
    //
    //             HASH_JOIN
    //             /       \
    //     INDEX_LOOKUP  INDEX_LOOKUP
    //       /  |  \        /   \
    //      a   b   c      d     e      <--- EDGES
    if (clone.operator in PhysicalOperator.JOIN_OPERATORS && clone.height() == 2) {

        val edges: List<Edge> = clone.children.flatMap { it.children.map { it.leaf!! } }

        // We can get | T r/l1 | from our Synopsis.
        var cardinality = synopsis.pairs(Pair(edges[0], edges[1])).toFloat()

        // | T r/l1/l2 | = | T r/l1 | * ( l1/l2.two / l1.in )
        for (index in 2 until edges.size) {

            val l1 = edges[index - 1]
            val l2 = edges[index]

            cardinality *= synopsis.two(Pair(l1, l2)) / synopsis.`in`(l1).toFloat()

        }

        // Return the result
        return cardinality.toLong()
    }
  ```

6. __Costing physical plans__

  Each physical operator has a cost associated to it which depends on the cardinality of the sets it operates on.

  For example, the cost of [hash-join](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/physicaloperators/HashJoin.kt#L38) is `2 * (M + N)`. Where `M` is the cardinality of set 1, and `N` is the cardinality of set 2.

  ```kotlin
    /**
     * Cost of Hash-join.
     */
    override fun cost(): Long {

        // The cost to produce results, i.e. 2 * (M + N)
        val myCost = 2 * (firstChild.cardinality + lastChild.cardinality)

        // Our input sets might be intermediate results, so take their cost into account.
        val cost1 = firstChild.cost()
        val cost2 = lastChild.cost()

        // Overflow check
        if (myCost == Long.MAX_VALUE || cost1 == Long.MAX_VALUE || cost2 == Long.MAX_VALUE) return Long.MAX_VALUE

        return myCost + cost1 + cost2
    }
  ```

7. __Save the cheapest physical plan__

  Once each enumerated physical plan has been costed, we save the cheapest physical plan. Since we work in a bottom-up fashion, after all iterations, we will have calculated the cheapest physical plan for the given logical plan.
