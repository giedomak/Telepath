package com.telepathdb.planner;

import com.telepathdb.costmodel.CostModel;
import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.stores.PathIdentifierStore;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate the best physical plan
 */
final public class Planner {

  public static ParseTree generate(ParseTree tree) {

    // Get an array of labels forming the label path
    // e.g. ['knows', 'worksFor', 'loves']
    List<String> labelPath = tree.postOrderTreeWalk()
        .filter(t -> t.isLeaf())
        .map(ParseTree::getLeaf)
        .collect(Collectors.toList());

//    Logger.debug(labelPath);

    int n = labelPath.size();
    int k = 3;

    HashMap<Long, ParseTree> bestPlans = new HashMap<>();

    // Init the BestPlan for all sub-paths of size 1
    labelPath.stream().forEach(
        label -> {
          long pathId = PathIdentifierStore.getPathIdentifierByEdgeLabel(label);
          bestPlans.put(
              pathId,
              ParseTree.createLookupTree(pathId)
          );
        }
    );

//    Logger.debug("Planner test");
//    Logger.debug(bestPlans.keySet());
//    bestPlans.values().forEach(ParseTreePrinter::printParseTree);

    for (int size = 2; size <= n; size++) {

      for (int offset = 0; offset <= (n - size); offset++) {

        List<String> Lsub = labelPath.subList(offset, offset + size);
        long LsubId = PathIdentifierStore.getPathIdentifierByEdgeLabel(Lsub);

        if (size <= k && !bestPlans.containsKey(LsubId)) {
          bestPlans.put(LsubId, ParseTree.createLookupTree(LsubId));
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

//          Logger.debug("Size: " + size + ", split: " + split + ", offset: " + offset);
//          Logger.debug("P1:");
//          Logger.debug(L1);
//          ParseTreePrinter.printParseTree(p1);
//          Logger.debug("P2:");
//          Logger.debug(L2);
//          ParseTreePrinter.printParseTree(p2);

          ParseTree currPlan = ParseTree.createConcatenationTree(p1, p2);

//          Logger.debug("currPlan:");
//          ParseTreePrinter.printParseTree(currPlan);

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
