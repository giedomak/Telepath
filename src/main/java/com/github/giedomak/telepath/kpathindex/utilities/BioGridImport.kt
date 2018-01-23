package com.github.giedomak.telepath.kpathindex.utilities

import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.utilities.Logger
import java.io.FileReader

object BioGridImport {

    /**
     * Imports a LUBM file.
     *
     * Assumes the format of the file resembles: `source_node_label target_node_label edge_label` separated by space.
     */
    fun run(kPathIndex: KPathIndex, lubmFile: String, dryRun: Boolean = false): Long {

        Logger.info("Importing: " + lubmFile)

        var importedLines: Long = 0
        var start = System.currentTimeMillis()

        FileReader(lubmFile).forEachLine { line ->

            // Split the line on space
            val split = line.split(" ")

            // split[0] is the starting node id
            val startNodeLabel = split[0]
            // split[1] is the end node id
            val endNodeLabel = split[1]
            // split[2] is the edge label
            val edgeLabel = split[2]

            // Put them into a list
            val nodes = listOf(Node(startNodeLabel), Node(endNodeLabel))
            val nodes2 = listOf(Node(endNodeLabel), Node(startNodeLabel))

            // Get the path identifier from the pathIdentifierStore
            val pathIdentifier = PathIdentifierStore.getPathIdByEdgeLabel(edgeLabel)

            // Insert the path and its inverse into the index
            kPathIndex.insert(Path(pathIdentifier, nodes), dryRun)
//            kPathIndex.insert(Path(pathIdentifier, nodes2), dryRun)

            // Yay, we've inserted one
            importedLines++


            if (importedLines % 5000 == 0L) {
                val secs = (System.currentTimeMillis() - start) / 1_000f
                val speed = (5000 / secs).toInt()
                Logger.debug("\r Currently inserted: $importedLines, Speed: $speed per second.", false)
                start = System.currentTimeMillis()
            }

        }

        // Set K to 1 for the kPathIndex because we only imported paths with 1 edge
        kPathIndex.k = 1

        Logger.info("Imported paths: " + importedLines)

        return importedLines
    }
}
