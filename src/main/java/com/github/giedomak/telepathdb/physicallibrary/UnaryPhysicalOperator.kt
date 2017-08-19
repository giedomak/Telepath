package com.github.giedomak.telepathdb.physicallibrary

interface UnaryPhysicalOperator {

    fun cost(cardinality: Long): Long

}