package com.github.giedomak.telepathdb.physicallibrary

import com.github.giedomak.telepathdb.TelepathDB
import com.github.giedomak.telepathdb.datamodels.Path
import com.github.giedomak.telepathdb.datamodels.PathPrefix
import java.util.stream.Stream

class IndexLookup(
        private val pathPrefix: PathPrefix
) : PhysicalOperator {

    override fun evaluate(): Stream<Path> {
        return TelepathDB.kPathIndex.search(pathPrefix)
    }

    companion object : UnaryPhysicalOperator {

        override fun cost(cardinality: Long): Long {
            return cardinality
        }

    }
}