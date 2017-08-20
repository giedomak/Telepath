package com.github.giedomak.telepathdb.physicallibrary.operators

import com.github.giedomak.telepathdb.datamodels.graph.PathStream

interface PhysicalOperator {

    fun evaluate(): PathStream

    fun cost(): Long

}