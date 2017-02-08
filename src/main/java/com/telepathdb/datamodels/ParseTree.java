/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

/**
 * Our internal representation of a query
 */
public class ParseTree {

  // Our public constants identifying our symbolic names
  public static final int
      KLEENE_STAR = 1, PLUS = 2, CONJUNCTION = 3, UNION = 4, LEAF = 5;

  private static final String[] SYMBOLIC_NAMES = {
      null, "KLEENE_STAR", "PLUS", "CONJUNCTION", "UNION", "LEAF"
  };

  // Can be one of above constants if this node is an internal node
  private int operator;

  // The payload when this node is a leaf
  private String leaf;

  private ParseTree left;
  private ParseTree right;

  private long id;
  static private long maxid = 1;

  public ParseTree() {
    this.id = maxid++;
  }

  //
  // ---------------- METHODS ----------------
  //

  public String getLeaf() {
    return leaf;
  }

  /**
   * Set the leaf and reset the operator; we can either be a leaf OR an internal node
   */
  public void setLeaf(String leaf) {
    this.leaf = leaf;
    this.operator = 0;
  }

  public int getOperator() {
    return operator;
  }

  /**
   * Set the operator and reset the leaf; we can either be a leaf OR an internal node
   */
  public void setOperator(int operator) {
    this.operator = operator;
    this.leaf = null;
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

  /**
   * Get the correct value of this node when it is a leaf or a internal node.
   *
   * @return String with the value of the Leaf or the Symbolic name of the operator
   */
  public String getLeafOrOperator() {
    if (getLeaf() == null) {
      return getSymbolicName();
    } else {
      return getLeaf();
    }
  }

  /**
   * Convert the operator identifier back to its symbolic name.
   *
   * @return String with the Symbolic name of the operator
   */
  private String getSymbolicName() {
    return SYMBOLIC_NAMES[operator];
  }

  /**
   * @return Boolean value indicating if this node is a leaf
   */
  public boolean isLeaf() {
    return leaf != null;
  }

  public long getId() { return id; }

  public boolean isRoot() {
    return id == 1;
  }
}
