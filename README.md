TelepathDB
=====
[![Build Status](https://travis-ci.org/giedomak/TelepathDB.svg?branch=master)](https://travis-ci.org/giedomak/TelepathDB)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/54b77ddc30294e5ca6ce0743f50811a4)](https://www.codacy.com/app/giedomak/TelepathDB?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=giedomak/TelepathDB&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/giedomak/TelepathDB/branch/master/graph/badge.svg)](https://codecov.io/gh/giedomak/TelepathDB)
[![Code Climate](https://codeclimate.com/github/giedomak/TelepathDB/badges/gpa.svg)](https://codeclimate.com/github/giedomak/TelepathDB)

Massive graph-structured data collections are ubiquitous in contemporary data management scenarios such as social networks, linked open data, and chemical compound databases.

The selection and manipulation of paths forms the core of querying graph datasets. Path indexing techniques can speed up this core functionality of querying graph datasets.

We propose a path-index based graph database engine.

## Documentation

The documentation can be found [here](https://giedomak.github.io/TelepathDB/telepathdb).

## Life of a Query

This section describes the essence of the life of a query within TelepathDB. Each heading contains links to its docs, test and source. In most cases, the test will give a clear insight into what each specific module produces.

1. __Query input__

  The user gives a regular path query as input. For example: `a/(b/c)`. Where `a`, `b` and `c` are edge labels, and `/` is interpreted as the concatenation logical operator.

2. __Parse the input__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.staticparser/-static-parser-r-p-q/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/staticparser/StaticParserRPQTest.kt#L19) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/staticparser/StaticParserRPQ.kt#L18)

  The query input is parsed into our internal representation of a logical plan. Our internal representation uses a tree datastructure:

              CONCATENATION
                /      \
               a   CONCATENATION
                      /   \
                     b     c

3. __Generate the cheapest physical plan__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.planner/-dynamic-programming-planner/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/planner/DynamicProgrammingPlannerTest.kt#L29) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/planner/DynamicProgrammingPlanner.kt#L20)

  Our planner uses the `DPsize` [algorithm](https://scholar.google.nl/scholar?q=Analysis+of+two+existing+and+one+new+dynamic+programming+algorithm+for+the+generation+of+optimal+bushy+join+trees+without+cross+products&btnG=&hl=en&as_sdt=0%2C5) as inspiration, which calculates the cheapest physical plan in a bottom-up fashion.

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

  4. __Enumerate operators__

    When two subtrees are contained through an operator in the logical plan, we'll calculate the cheapest physical plan for their combination. Remember we already know the cheapest physical plans for both subtrees.

    As an example, let's say we've got two subtrees contained through the `CONCATENATION` operator. We enumerate the logical operator into hash-join, nested-loop-join and index-lookup.

  5. __Costing physical plans__

    Each physical operator has a cost associated to it which depends on the cardinality of the sets it operates on.

    For example, the cost of hash-join is `2 * (M + N)`.

  6. __Save the cheapest physical plan__

    Once each enumerated physical plan has been costed, we save the cheapest physical plan. Since we work in a bottom-up fashion, after all iterations, we will have calculated the cheapest physical plan for the given logical plan.

## Architecture

Schematic overview of the architecture:

![](src/main/resources/pathdb.png?raw=true)
