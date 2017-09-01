## Adding a new physical operator

  To add a new physical operator, we need a couple of things:

  - Implement the PhysicalOperator interface.
  - Enumeration from the logical operator into our new physical operator.
  - Cardinality estimation of the result set from our new physical operator.
  - Cost of the evaluation of our new physical operator.
  - Evaluation of our new physical operator.

### PhysicalOperator implementation [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.physicaloperators/-physical-operator/index.html) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/physicaloperators/PhysicalOperator.kt#L13)

  The companion object from the PhysicalOperator class is responsible for the mapping from physical operator constants to the actual implementation. Implement those [here](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/physicaloperators/PhysicalOperator.kt#L41).

  The actual implementation is described in the Costing an Evaluation section.

  Code snippet of the symbolic mapping:

  ```kotlin
  companion object {

      // ------ CONSTANTS ------

      const val LEAF = 0

      const val INDEX_LOOKUP = 1

      const val HASH_JOIN = 2
      const val NESTED_LOOP_JOIN = 3

      const val UNION = 4

      // ------ COLLECTIONS -------

      val JOIN_OPERATORS = listOf(HASH_JOIN, NESTED_LOOP_JOIN)

      // ------ FUNCTIONS -------

      /**
       * Convert the operators constants to an actual [PhysicalOperator] instance.
       *
       * @param physicalPlan which holds the operator constant.
       * @return The PhysicalOperator instance which has knowledge of the physical plan.
       */
      fun getPhysicalOperator(physicalPlan: PhysicalPlan): PhysicalOperator? {

          return when (physicalPlan.operator) {

              LEAF -> null

              INDEX_LOOKUP -> IndexLookup(physicalPlan)

              HASH_JOIN -> HashJoin(physicalPlan)
              NESTED_LOOP_JOIN -> NestedLoopJoin(physicalPlan)

              UNION -> Union(physicalPlan)

              else -> TODO("Gotta catch em all")
          }
      }
  }
  ```

### Enumerate operator [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.planner.enumerator/-simple-enumerator/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/planner/enumerator/SimpleEnumeratorTest.kt) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/planner/enumerator/SimpleEnumerator.kt#L11)

  This example combines two physical plans by enumerating over the applicable physical operators for the `CONCATENATION` logical operator:

              INDEX_LOOKUP       INDEX_LOOKUP
                /     \            /     \
               a      b           c      d

  Expected:

           INDEX_LOOKUP              HASH_JOIN                NESTED_LOOP_JOIN
            / |  |  \                  /    \                     /       \
           a  b  c   d       INDEX_LOOKUP INDEX_LOOKUP    INDEX_LOOKUP INDEX_LOOKUP
                               /     \      /    \          /     \      /    \
                              a      b     c     d         a      b     c     d

  The [code snippet](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/planner/enumerator/SimpleEnumerator.kt#L37) making this possible:

  ```kotlin
      private fun enumerateConcatenation(tree1: PhysicalPlan, tree2: PhysicalPlan): Sequence<PhysicalPlan> {

        val physicalPlans = mutableListOf<PhysicalPlan>()

        // Check if an INDEX_LOOKUP is applicable.
        val plan = tree1.merge(tree2, PhysicalOperator.INDEX_LOOKUP).flatten()

        // If the height of this tree is 1 (max number of edges to any leaf), AND the number of children
        // is smaller or equal to the k-value of our index, we can do an INDEX_LOOKUP!
        if (plan.height() == 1 && plan.children.size <= plan.query.telepathDB.kPathIndex.k) {
            physicalPlans.add(plan)
        }

        // Don't forget to enumerate all the JOIN_OPERATORS
        PhysicalOperator.JOIN_OPERATORS.forEach {
            physicalPlans.add(tree1.merge(tree2, it))
        }

        return physicalPlans.asSequence()
    }
  ```

### Cardinality estimation [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.cardinalityestimation/-k-path-index-cardinality-estimation/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/cardinalityestimation/KPathIndexCardinalityEstimationTest.kt#L23) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/cardinalityestimation/KPathIndexCardinalityEstimation.kt#L16)

  As you can see in the current implementation, cardinality estimates for `JOIN_OPERATORS` will just take the max cardinality of its two datasets. The `UNION` physical operator will get you the sum of the cardinalities of its children.

  Code snippet:

  ```kotlin
  /**
   * Returns the cardinality of a given physicalPlan.
   *
   * This method will recursively calculate the cardinality for its children in order to get the cardinality
   * for the root.
   *
   * @param physicalPlan The root of the tree for which we want to get the cardinality.
   * @return The cardinality of the given physicalPlan.
   */
  override fun getCardinality(physicalPlan: PhysicalPlan): Long {

      return when (physicalPlan.operator) {

          PhysicalOperator.INDEX_LOOKUP -> getCardinality(physicalPlan.pathIdOfChildren())

          in PhysicalOperator.JOIN_OPERATORS -> {
              val d1 = getCardinality(physicalPlan.children.first())
              val d2 = getCardinality(physicalPlan.children.last())
              Math.max(d1, d2)
          }

          PhysicalOperator.UNION -> {
              getCardinality(physicalPlan.children.first()) + getCardinality(physicalPlan.children.last())
          }

          else -> TODO("You forgot one!")
      }
  }
  ```

### Costing & Evaluation

  Costing and Evaluation are both delegated to the PhysicalOperator implementation to maintain a more object-oriented approach.

  See the hash-join implementation as a reference. [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.physicaloperators/-hash-join/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/physicaloperators/HashJoinTest.kt#L18) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/physicaloperators/HashJoin.kt#L16)

  Code snippet of the hash-join implementation:

  ```kotlin
  /**
   * Hash-join physical operator.
   *
   * @property physicalPlan The physical plan holds information regarding the sets on which to operate on.
   * @property firstChild The first set of data to operate on, which is a [PhysicalOperator] itself.
   * @property lastChild The last set of data to operate on, which is a [PhysicalOperator] itself.
   */
  class HashJoin(override val physicalPlan: PhysicalPlan) : PhysicalOperator {

      /**
       * Evaluate the hash-join.
       *
       * @return A stream with the concatenated paths.
       */
      override fun evaluate(): PathStream {
          return OpenHashJoin(
                  firstChild.evaluate(),
                  lastChild.evaluate(),
                  physicalPlan.query.telepathDB
          ).evaluate()
      }

      /**
       * Cost of Hash-join.
       */
      override fun cost(): Long {

          // The cost to produce results, i.e. 2 * (M + N)
          val myCost = 2 * (firstChild.cardinality() + lastChild.cardinality())

          // Our input sets might be intermediate results, so take their cost into account.
          val intermediateResultsCost = firstChild.cost() + lastChild.cost()

          return myCost + intermediateResultsCost
      }
  }
  ```
