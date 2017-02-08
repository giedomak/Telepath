package com.telepathdb.evaluationengine;

import com.google.common.collect.Lists;

import com.telepathdb.datamodels.Edge;
import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathIdentifierStore;
import com.telepathdb.datamodels.PathPrefix;
import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.physicallibrary.MergeJoin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by giedomak on 08/02/2017.
 */
public class EvaluationEngine {

  private KPathIndex kPathIndex;
  private HashMap<Long, List<Path>> intermediateResults;

  public EvaluationEngine(KPathIndex kPathIndex) {
    this.kPathIndex = kPathIndex;
    this.intermediateResults = new HashMap<>();
  }

  public Stream<Path> evaluate(ParseTree parseTree) throws IOException {

    if (parseTree == null) return null;

    // Make sure we do an Postorder treewalk, this way we gather all the information from the leafs first
    evaluate(parseTree.getLeft());
    evaluate(parseTree.getRight());

    List<Path> results = new ArrayList<>();

    // Collect results from the leafs and put them in the intermediateResults HashMap
    if (parseTree.isLeaf()) {
      long pathIdentifier = PathIdentifierStore.getPathIdentifierByEdgeSet(Arrays.asList(new Edge(Long.parseLong(parseTree.getLeaf()))));
      PathPrefix search = new PathPrefix(pathIdentifier, 2);
      Iterable<Path> iterator = kPathIndex.search(search);

      results = Lists.newArrayList(iterator);
      intermediateResults.put(parseTree.getId(), results);

      System.out.println("Search kpathindex: " + parseTree.getLeaf() + ": " + results.size());

    } else {
      // Perform the Operations
      switch (parseTree.getOperator()) {

        case ParseTree.UNION:
          results = MergeJoin.perform(intermediateResults.get(parseTree.getLeft().getId()), intermediateResults.get(parseTree.getRight().getId())).collect(Collectors.toList());
          break;

      }
    }

    return results.stream();
  }

}
