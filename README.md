TelepathDB
=====
[![Build Status](https://travis-ci.org/giedomak/TelepathDB.svg?branch=master)](https://travis-ci.org/giedomak/TelepathDB)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/54b77ddc30294e5ca6ce0743f50811a4)](https://www.codacy.com/app/giedomak/TelepathDB?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=giedomak/TelepathDB&amp;utm_campaign=Badge_Grade)
[![Code Climate](https://codeclimate.com/github/giedomak/TelepathDB/badges/gpa.svg)](https://codeclimate.com/github/giedomak/TelepathDB)
[![codebeat badge](https://codebeat.co/badges/ffa0cab1-0edc-4900-b96c-68a17c73e3a8)](https://codebeat.co/projects/github-com-giedomak-telepathdb-master)
[![codecov](https://codecov.io/gh/giedomak/TelepathDB/branch/master/graph/badge.svg)](https://codecov.io/gh/giedomak/TelepathDB)

Massive graph-structured data collections are ubiquitous in contemporary data management scenarios such as social networks, linked open data, and chemical compound databases.

The selection and manipulation of paths forms the core of querying graph datasets. Path indexing techniques can speed up this core functionality of querying graph datasets.

We propose a path-index based graph database engine.

## Documentation

The documentation can be found [here](https://giedomak.github.io/TelepathDB/telepathdb) and a schematic overview of the architecture can be found [here](https://github.com/giedomak/TelepathDB/tree/master/src/main/resources).

## Life of a Query

This section describes the essence of the life of a query within TelepathDB. Each heading contains links to its docs, test and source. In most cases, the test will give a clear insight into what each specific module produces.

1. __Query input__

  The user gives a regular path query as input. For example:

  ```
    a/(b/c)
  ```

  Where `a`, `b` and `c` are edge labels, and `/` is interpreted as the concatenation logical operator.

2. __Parse the input__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.staticparser/-static-parser-r-p-q/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/staticparser/StaticParserRPQTest.kt#L19) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/staticparser/StaticParserRPQ.kt#L18)

  The query input is parsed into our internal representation of a logical plan. Our internal representation uses a tree datastructure:

              CONCATENATION
                /      \
               a   CONCATENATION
                      /   \
                     b     c

3. __Generate the cheapest physical plan__ [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.planner/-dynamic-programming-planner/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/planner/DynamicProgrammingPlannerTest.kt#L29) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/planner/DynamicProgrammingPlanner.kt#L20)

  Our planner uses the `DPsize` [algorithm](https://scholar.google.nl/scholar?q=Analysis+of+two+existing+and+one+new+dynamic+programming+algorithm+for+the+generation+of+optimal+bushy+join+trees+without+cross+products&btnG=&hl=en&as_sdt=0%2C5) as inspiration, which calculates the cheapest physical plan in a bottom-up fashion.

  Since this phase is one of the main contributions, an in-depth explanation can be found [here](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/planner).

              INDEX_LOOKUP
                /  |  \
               a   b   c

4. __Evaluate the physical plan__

  The physical plan is evaluated in a bottom-up fashion. All intermediate results are materialized through our MemoryManager [(docs)](https://giedomak.github.io/TelepathDB/telepathdb/com.github.giedomak.telepathdb.memorymanager/-memory-manager/index.html) [(test)](https://github.com/giedomak/TelepathDB/blob/master/src/test/java/com/github/giedomak/telepathdb/memorymanager/SimpleMemoryManagerTest.kt#L25) [(source)](https://github.com/giedomak/TelepathDB/blob/master/src/main/java/com/github/giedomak/telepathdb/memorymanager/SimpleMemoryManager.kt#L23).

  Using [PathDB](https://github.com/maxsumrall/PathDB) to gather the paths satisfying our query:

                kPathIndex.search(
                        PathPrefix(
                                physicalPlan.pathIdOfChildren()
                        )
                )

5. __Visualize results__

  At the time of writing, results will be shown to the user through a command-line interface.

  ```
    TelepathDB: >>>>> Results:
    TelepathDB: Path(pathId=9, nodes=[Node(id=10), Node(id=12), Node(id=14)])
    TelepathDB: Path(pathId=9, nodes=[Node(id=10), Node(id=12), Node(id=8772)])
    TelepathDB: Number of results: 2, after 5 ms
    TelepathDB: ----------------------------
  ```
