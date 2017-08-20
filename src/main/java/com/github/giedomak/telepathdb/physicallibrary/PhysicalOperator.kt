package com.github.giedomak.telepathdb.physicallibrary

import com.github.giedomak.telepathdb.datamodels.Path
import java.util.stream.Stream

interface PhysicalOperator {

    fun evaluate(): Stream<Path>

}