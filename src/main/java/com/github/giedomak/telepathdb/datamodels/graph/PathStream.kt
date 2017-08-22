package com.github.giedomak.telepathdb.datamodels.graph

import java.util.stream.Stream

data class PathStream(val paths: Stream<Path> = Stream.empty())