package com.github.giedomak.telepathdb.datamodels

import java.util.stream.Stream

class PathStream(val paths: Stream<Path>) {

    var cardinality = Int.MAX_VALUE

//    constructor(pathStream: Stream<Path>, cardinality: Int) : this(pathStream) {
//        this.cardinality = cardinality
//    }
}