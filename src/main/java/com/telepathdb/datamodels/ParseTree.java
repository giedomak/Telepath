/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

/**
 * Created by giedomak on 20/01/2017.
 */
public class ParseTree {

  // Our public constants identifying our symbolic names
  public static final int
      KLEENE_STAR = 1, PLUS = 2, CONJUNCTION = 3, UNION = 4, LEAF = 5;

  private static final String[] SYMBOLIC_NAMES = {
      null, "KLEENE_STAR", "PLUS", "CONJUNCTION", "UNION", "LEAF"
  };

  // Can be one of above constants
  private int operator;

  // The payload when this node is a leaf
  private String leaf;

  private ParseTree left;
  private ParseTree right;

  public ParseTree() {
  }

  public String getLeaf() {
    return leaf;
  }

  public void setLeaf(String leaf) {
    this.leaf = leaf;
  }

  public int getOperator() {
    return operator;
  }

  public void setOperator(int operator) {
    this.operator = operator;
  }

  public ParseTree getLeft() {
    return left;
  }

  public void setLeft(ParseTree left) {
    this.left = left;
  }

  public ParseTree getRight() {
    return right;
  }

  public void setRight(ParseTree right) {
    this.right = right;
  }

  public String getLeafOrOperator() {
    if (getLeaf() == null) {
      return getSymbolicName();
    } else {
      return getLeaf();
    }
  }

  private String getSymbolicName() {
    return SYMBOLIC_NAMES[operator];
  }

}
