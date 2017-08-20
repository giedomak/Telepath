package com.github.giedomak.telepathdb.physicallibrary

interface BinaryPhysicalOperator {

    fun cost(cardinality1: Long, cardinality2: Long): Long

}