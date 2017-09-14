package com.github.giedomak.telepathdb.kpathindex.utilities

import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.kpathindex.KPathIndex
import com.github.giedomak.telepathdb.utilities.Logger
import java.io.BufferedReader
import java.io.FileReader

object LUBMImport {

    /**
     * Imports a LUBM file.
     *
     * Assumes the format of the file resembles: `<node_label> <http://edge_label> <node_label>` separated by space.
     */
    fun run(kPathIndex: KPathIndex, lubmFile: String): Long {

        Logger.info("Importing: " + lubmFile)

        var importedLines: Long = 0

        BufferedReader(FileReader(lubmFile)).use { br ->

            // Let's start with the first line
            var line: String? = br.readLine()

            while (line != null) {
                // Split the line on space
                val split = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                // split[0] is the starting node id
                val startNodeLabel = split[0].replace("<", "").replace(">", "")
                // split[1] is the edge label
                var edgeLabel = split[1].replace("<", "").replace(">", "")
                // split[2] is the end node id
                val endNodeLabel = split[2].replace("<", "").replace(">", "")

                // Prepare data
                edgeLabel = edgeLabel.replace("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#", "")

                // Skip noisy stuff
                val whiteLabels = listOf("subOrganizationOf", "teacherOf", "undergraduateDegreeFrom",
                        "mastersDegreeFrom", "doctoralDegreeFrom", "worksFor", "researchInterest",
                        "headOf", "memberOf", "takesCourse", "advisor", "publicationAuthor", "teachingAssistantOf")

                if (whiteLabels.contains(edgeLabel)) {

                    // Put them into a list
                    val nodes = listOf(Node(startNodeLabel), Node(endNodeLabel))

                    // Get the path identifier from the pathIdentifierStore
                    val pathIdentifier = PathIdentifierStore.getPathIdByEdgeLabel(edgeLabel)

                    // Insert into the index
                    kPathIndex.insert(Path(pathIdentifier, nodes))
                    kPathIndex.insert(Path(pathIdentifier, nodes).inverse())

                    // Yay, we've inserted one
                    importedLines++

                }

                // Read the next line
                line = br.readLine()
            }
        }

        // Set K to 1 for the kPathIndex because we only imported paths with 1 edge
        kPathIndex.k = 1

        Logger.info("Imported paths: " + importedLines)

        return importedLines
    }
}