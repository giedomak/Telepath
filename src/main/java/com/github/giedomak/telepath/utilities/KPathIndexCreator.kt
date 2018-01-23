package com.github.giedomak.telepath.utilities

import com.github.giedomak.telepath.Telepath
import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.kpathindex.KPathIndexDisk
import com.github.giedomak.telepath.kpathindex.utilities.BioGridImport
import com.github.giedomak.telepath.kpathindex.utilities.KExtender
import java.io.File

object KPathIndexCreator {

//    val dataset = "/Users/giedomak/Desktop/BioGrid2.edge"
    val dataset = "./BioGrid2.edge"

//    val dataset = "/Users/giedomak/Documents/Apps/lubm-uba/10/Universities.nt"
//    val dataset = "src/test/resources/advogato-graph-2014-07-07.dot"

    val indexLocation = File("./")
//    val indexLocation = File("/Users/giedomak/Desktop/biogrid2/")
//    val indexLocation = File("/Users/giedomak/Desktop/10k1/")
//    val indexLocation = File("/Users/giedomak/Desktop/Advogatok1/")

    val k = 2

    @JvmStatic
    fun main(args: Array<String>?) {

        // Set the correct index
        Telepath.kPathIndex = KPathIndexDisk(dir = indexLocation)
        Telepath.kPathIndex.insertCallback = null

        // Make sure our Nodes and Paths are known in our PathIdentifierStore and NodeIdentifierStore
        BioGridImport.run(Telepath.kPathIndex, dataset)
//        LUBMImport.run(Telepath.kPathIndex, dataset)
//        AdvogatoImport.run(Telepath.kPathIndbio
// ex, dataset)

        Logger.debug("Number of nodes: " + Node.numberOfNodes())

        // Extend to k
        KExtender.run(Telepath.kPathIndex, k)
    }
}