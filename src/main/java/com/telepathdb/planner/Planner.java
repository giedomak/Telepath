package com.telepathdb.planner;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.utilities.Logger;

import java.util.stream.Collectors;

/**
 * Generate the best physical plan
 */
final public class Planner {

  public static ParseTree generate(ParseTree tree) {

    Logger.debug(
        tree.postOrderTreeWalk()
            .filter(t -> t.isLeaf())
            .map(ParseTree::getLeaf)
            .collect(Collectors.toList())
    );

    return tree;
  }
}
