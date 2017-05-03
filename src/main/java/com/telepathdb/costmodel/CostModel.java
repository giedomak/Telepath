package com.telepathdb.costmodel;

import com.telepathdb.datamodels.ParseTree;

final public class CostModel {

  static public int cost(ParseTree tree) {

    return tree.level();
  }
}
