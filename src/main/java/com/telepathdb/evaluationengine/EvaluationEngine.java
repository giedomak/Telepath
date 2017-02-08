package com.telepathdb.evaluationengine;

import com.telepathdb.datamodels.Edge;
import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathIdentifierStore;
import com.telepathdb.datamodels.PathPrefix;
import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.physicallibrary.MergeJoin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by giedomak on 08/02/2017.
 */
public class EvaluationEngine {

  private KPathIndex kPathIndex;
  private HashMap<Long, List<Path>> intermediateResults; // In-memory for now

  public EvaluationEngine(KPathIndex kPathIndex) {
    this.kPathIndex = kPathIndex;
    this.intermediateResults = new HashMap<>();
  }

  public Stream<Path> evaluate(ParseTree parseTree) throws IOException {

    if (parseTree == null) return null;

    // Make sure we do an Postorder treewalk, this way we gather all the information from the leafs first
    evaluate(parseTree.getLeft());
    evaluate(parseTree.getRight());

    Stream<Path> results;

    // Collect results from the leafs and put them in the intermediateResults HashMap
    if (parseTree.isLeaf()) {

      long pathIdentifier = PathIdentifierStore.getPathIdentifierByEdgeSet(Arrays.asList(new Edge(Long.parseLong(parseTree.getLeaf()))));
      PathPrefix search = new PathPrefix(pathIdentifier, 2);
      results = StreamSupport.stream(kPathIndex.search(search).spliterator(), false);

    } else {
      // Perform the Operations
      switch (parseTree.getOperator()) {

        case ParseTree.UNION:
          results = MergeJoin.perform(intermediateResults.get(parseTree.getLeft().getId()), intermediateResults.get(parseTree.getRight().getId()));
          break;

        default:
          throw new IllegalArgumentException("Operator not yet implemented for " + parseTree.getLeafOrOperator() + "!");

      }
    }

    if (parseTree.isRoot()) {
      // Make sure we return the stream when this node was the root
      return results;
    } else {
      List<Path> collectedResults = results.collect(Collectors.toList());
      System.out.println("Itermediateresult: " + parseTree.getLeafOrOperator() + ": " + collectedResults.size());
      intermediateResults.put(parseTree.getId(), collectedResults);
    }

    return null;
  }
}
