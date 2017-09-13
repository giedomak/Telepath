# Architecture

Schematic overview of the architecture:

![Architecture](pathdb.png?raw=true)

Directory structure:

```
.
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── github
    │   │           └── giedomak
    │   │               └── telepathdb
    │   │                   ├── cardinalityestimation
    │   │                   │   └── synopsis
    │   │                   ├── costmodel
    │   │                   ├── datamodels
    │   │                   │   ├── graph
    │   │                   │   ├── integrations
    │   │                   │   ├── plans
    │   │                   │   │   └── utilities
    │   │                   │   └── stores
    │   │                   ├── evaluationengine
    │   │                   ├── kpathindex
    │   │                   │   └── utilities
    │   │                   ├── memorymanager
    │   │                   │   └── spliterator
    │   │                   ├── physicaloperators
    │   │                   ├── planner
    │   │                   │   └── enumerator
    │   │                   ├── staticparser
    │   │                   │   └── rpq
    │   │                   └── utilities
    │   └── resources
    │       └── antlr4
    │           ├── rpq
    │           │   └── examples
    │           └── sparql
    │               └── examples
    └── test
        ├── java
        │   └── com
        │       └── github
        │           └── giedomak
        │               └── telepathdb
        │                   ├── cardinalityestimation
        │                   │   └── synopsis
        │                   ├── datamodels
        │                   │   └── plans
        │                   │       └── utilities
        │                   ├── evaluationengine
        │                   ├── integrationtests
        │                   ├── kpathindex
        │                   │   └── utilities
        │                   ├── memorymanager
        │                   ├── physicaloperators
        │                   ├── planner
        │                   │   └── enumerator
        │                   ├── staticparser
        │                   └── utilities
        └── resources
            └── mockito-extensions
```
