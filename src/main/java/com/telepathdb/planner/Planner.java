/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.planner;

import com.telepathdb.costmodel.CostModel;
import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.stores.PathIdentifierStore;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate the best physical plan for a given ParseTree
 */
final public class Planner {

  public static ParseTree generate(ParseTree tree) {

    // Get an array of labels forming the label path
    // e.g. ['knows', 'worksFor', 'loves']
    List<String> labelPath = tree.postOrderTreeWalk()
        .filter(t -> t.isLeaf())
        .map(ParseTree::getLeafOrOperator)
        .collect(Collectors.toList());

    int n = labelPath.size();
    int k = 3;

    HashMap<Long, ParseTree> bestPlans = new HashMap<>();

    // Init the BestPlan for all sub-paths of size 1
    labelPath.stream().forEach(
        label -> {
          long pathId = PathIdentifierStore.getPathIdentifierByEdgeLabel(label);
          bestPlans.put(
              pathId,
              ParseTree.Companion.createLookupTree(pathId)
          );
        }
    );

    for (int size = 2; size <= n; size++) {

      for (int offset = 0; offset <= (n - size); offset++) {

        List<String> Lsub = labelPath.subList(offset, offset + size);
        long LsubId = PathIdentifierStore.getPathIdentifierByEdgeLabel(Lsub);

        if (size <= k && !bestPlans.containsKey(LsubId)) {
          bestPlans.put(LsubId, ParseTree.Companion.createLookupTree(LsubId));
        }

        if (bestPlans.containsKey(LsubId)) {
          continue;
        }

        for (int split = 1; split < size; split++) {

          List<String> L1 = labelPath.subList(offset, offset + split);
          long L1Id = PathIdentifierStore.getPathIdentifierByEdgeLabel(L1);

          List<String> L2 = labelPath.subList(offset + split, offset + size);
          long L2Id = PathIdentifierStore.getPathIdentifierByEdgeLabel(L2);

          ParseTree p1 = bestPlans.get(L1Id);
          ParseTree p2 = bestPlans.get(L2Id);
          ParseTree currPlan = ParseTree.Companion.createConcatenationTree(p1, p2);

          if (!bestPlans.containsKey(LsubId) ||
              CostModel.cost(currPlan) < CostModel.cost(bestPlans.get(LsubId))) {
            bestPlans.put(LsubId, currPlan);
          }
        }
      }
    }

    return bestPlans.get(PathIdentifierStore.getPathIdentifierByEdgeLabel(labelPath));
  }
}
