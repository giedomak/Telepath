/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.kpathindex.utilities;

import com.telepathdb.datamodels.Node;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.stores.PathIdentifierStore;
import com.telepathdb.datamodels.utilities.Logger;
import com.telepathdb.kpathindex.KPathIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to import graphs generated by gMark into the kpathindex.
 * gMark: https://github.com/graphMark/gmark
 */
public final class GMarkImport {

  /**
   * Imports a gMark file
   * <p>
   * Assumes the format of the file resembles: node_id; edge_label; node_id
   * For example: 42 4 6
   * Meaning a (directed) edge from node 42 to node 6, with edgelabel 4.
   */
  public static long run(KPathIndex kPathIndex, String gMarkFile) throws IOException {

    Logger.info("Importing: " + gMarkFile);

    long importedLines = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(gMarkFile))) {
      String line = br.readLine();

      while (line != null) {
        // Split the line on space
        String[] split = line.split(" ");

        // split[0] is the starting node id
        long startNodeID = Long.parseLong(split[0]);
        // split[1] is the edgelabel
        String edgeLabel = split[1];
        // split[2] is the end node id
        long endNodeID = Long.parseLong(split[2]);

        // Put them into an array
        List<Node> nodes = new ArrayList<>(2);
        nodes.add(new Node(startNodeID));
        nodes.add(new Node(endNodeID));

        // Get the path identifier from the pathIdentifierStore
        long pathIdentifier = PathIdentifierStore.getPathIdentifierByEdgeLabel(edgeLabel);

        // Insert into the kpathindex
        kPathIndex.insert(new Path(pathIdentifier, nodes));

        importedLines++;
        line = br.readLine();
      }
    }

    // Set K to 1 for the kPathIndex becuase we only imported paths with 1 edge
    kPathIndex.setK(1);

    Logger.info("Imported paths: " + importedLines);

    return importedLines;
  }
}
