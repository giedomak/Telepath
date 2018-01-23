package com.github.giedomak.telepath.kpathindex.utilities

import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.kpathindex.KPathIndex
import com.github.giedomak.telepath.utilities.Logger
import java.io.FileReader

object LUBMImport {

    /**
     * Imports a LUBM file.
     *
     * Assumes the format of the file resembles: `<node_label> <http://edge_label> <node_label>` separated by space.
     */
    fun run(kPathIndex: KPathIndex, lubmFile: String, dryRun: Boolean = false): Long {

        Logger.info("Importing: " + lubmFile)

        var importedLines: Long = 0

        FileReader(lubmFile).forEachLine { line ->

            // Split the line on space
            val split = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // split[0] is the starting node id
            val startNodeLabel = split[0]
                    .replace("<", "")
                    .replace(">", "")
            // split[1] is the edge label
            val edgeLabel = split[1]
                    .replace("<", "")
                    .replace(">", "")
                    .replace("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#", "")
            // split[2] is the end node id
            val endNodeLabel = split[2]
                    .replace("<", "")
                    .replace(">", "")

            // Skip noisy stuff
            val whiteLabels = listOf("subOrganizationOf", "teacherOf", "undergraduateDegreeFrom",
                    "mastersDegreeFrom", "doctoralDegreeFrom", "worksFor", "researchInterest",
                    "headOf", "memberOf", "takesCourse", "advisor", "publicationAuthor", "teachingAssistantOf")

            if (whiteLabels.contains(edgeLabel)) {

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
