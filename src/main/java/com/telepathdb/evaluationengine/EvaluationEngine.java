/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.evaluationengine;

import com.telepathdb.datamodels.Edge;
import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathPrefix;
import com.telepathdb.datamodels.stores.PathIdentifierStore;
import com.telepathdb.datamodels.utilities.Logger;
import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.memorymanager.MemoryManager;
import com.telepathdb.physicallibrary.PhysicalLibrary;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by giedomak on 08/02/2017.
 */
public class EvaluationEngine {

  private KPathIndex kPathIndex;

  public EvaluationEngine(KPathIndex kPathIndex) {
    this.kPathIndex = kPathIndex;
  }

  public Stream<Path> evaluate(ParseTree parseTree) {

    if (parseTree == null) return null;
    if (parseTree.isLeaf()) return null;

    // Make sure we do an Postorder treewalk, this way we gather all the information from the leafs first
    for (ParseTree child : parseTree.getChildren()) {
      evaluate(child);
    }

    Stream<Path> results = Stream.empty();

    // Perform the Operations
    switch (parseTree.getOperatorId()) {

      case ParseTree.LOOKUP:
        // Collect results from the leafs and put them in the intermediateResults HashMap
        List<Edge> edges = parseTree.getChildren().stream().map(ParseTree::getEdge).collect(Collectors.toList());
        long pathIdentifier = PathIdentifierStore.INSTANCE.getPathIdByEdges(edges);
        PathPrefix search = new PathPrefix(pathIdentifier);
        results = kPathIndex.search(search);
        break;

      case ParseTree.UNION:
        results = PhysicalLibrary.union(getChild(parseTree, 0), getChild(parseTree, 1));
        break;

      case ParseTree.CONCATENATION:
        results = PhysicalLibrary.concatenation(getChild(parseTree, 0), getChild(parseTree, 1));
        break;

      default:
        throw new IllegalArgumentException("EvaluationEngine: operatorId not yet implemented for " + parseTree.getLeafOrOperator() + "!");

    }

    if (parseTree.isRoot()) {
      // Make sure we return the stream when this node was the root
      return results;
    } else {
      Logger.INSTANCE.debug("Itermediateresult: " + parseTree.getLeafOrOperator());
      MemoryManager.put(parseTree.getId(), results);
    }

    return null;
  }

  protected Stream<Path> getChild(ParseTree parseTree, int index) {
    return MemoryManager.get(parseTree.getChild(index).getId());
  }
}
