package com.github.giedomak.telepath.kpathindex.utilities

import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.utilities.Logger
import java.io.FileReader

object AdvogatoImport {

    fun run(kPathIndex: KPathIndex, advogatoFile: String, dryRun: Boolean = false): Long {

        Logger.info("Importing: " + advogatoFile)

        var importedLines: Long = 0

        FileReader(advogatoFile).forEachLine { line ->

            // Use a Regex to get the node and edge labels
            val split = Regex("^\\s+(.+)\\s(?:->)\\s(.+)\\s(?:\\[level=\")(.+)(?:\"];)(?:.*)\$").matchEntire(line)?.groupValues

            if (split != null) {

                // split[1] is the starting node id
                val startNodeLabel = split[1]
                // split[2] is the end node id
                val endNodeLabel = split[2]
                // split[3] is the edge label
                val edgeLabel = split[3].toLowerCase()

                // Put them into a list
                val nodes = listOf(Node(startNodeLabel), Node(endNodeLabel))

                // Get the path identifier from the pathIdentifierStore
                val pathIdentifier = PathIdentifierStore.getPathIdByEdgeLabel(edgeLabel)

                // Insert the path and its inverse into the index
                kPathIndex.insert(Path(pathIdentifier, nodes), dryRun)
                kPathIndex.insert(Path(pathIdentifier, nodes).inverse(), dryRun)

                // Yay, we've inserted one
                importedLines++

                if (importedLines % 5000 == 0L) {
                    Logger.debug("\r Currently inserted: $importedLines", false)
                }
            }
        }

        // Set K to 1 for the kPathIndex because we only imported paths with 1 edge
        kPathIndex.k = 1

        Logger.info("Imported paths: " + importedLines)

        return importedLines
    }

}