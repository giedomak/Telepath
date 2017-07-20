/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels;

import com.telepathdb.datamodels.stores.PathIdentifierStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Our internal representation of a query; how we can parse the user-input.
 */
public class ParseTree implements Cloneable {

  // Our public constants identifying our symbolic names
  public static final int
      KLEENE_STAR = 1, PLUS = 2, CONCATENATION = 3, UNION = 4, LEAF = 5, LOOKUP = 6;

  private static final String[] SYMBOLIC_NAMES = {
      null, "KLEENE_STAR", "PLUS", "CONCATENATION", "UNION", "LEAF", "LOOKUP"
  };
  static private long maxid = 1;
  final private long id;
  // Can be one of above constants if this node is an internal node
  private int operator;
  // The payload when this node is a leaf
  private String leaf;
  private Edge edge;
  private boolean root = false;
  private List<ParseTree> children;

  public ParseTree() {
    this.children = new ArrayList<ParseTree>();
    this.id = maxid++;
  }

  public ParseTree(boolean isRoot) {
    this();
    this.setRoot(isRoot);
  }

  //
  // ---------------- METHODS ----------------
  //

  static public ParseTree createLookupTree(long pathId) {

    List<Edge> edges = PathIdentifierStore.getEdgeSet(pathId);

    ParseTree tree = new ParseTree(true);

    tree.setOperator(LOOKUP);

    edges.stream().map(edge -> {
      ParseTree child = new ParseTree();
      child.setLeaf(edge);
      return child;
    }).forEach(tree.children::add);

    return tree;
  }

  static public ParseTree createConcatenationTree(ParseTree p1, ParseTree p2) {

    ParseTree tree = new ParseTree(true);

    tree.setOperator(CONCATENATION);

    tree.setChild(0, p1);
    tree.setChild(1, p2);

    return tree;
  }

  public String getLeaf() {
    return leaf;
  }

  public Edge getEdge() {
    return edge;
  }

  /**
   * Set the leaf and reset the operator; we can either be a leaf OR an internal node
   */
  public void setLeaf(String leaf) {
    this.leaf = leaf;
    this.operator = 0;
    this.edge = null;
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
    this.edge = null;
  }

  public boolean hasChild(int index) {
    return getChild(index) != null;
  }

  public ParseTree getChild(int index) {
    if (children.size() > index) {
      return children.get(index);
    }
    return null;
  }

  public List<ParseTree> getChildren() {
    return children;
  }

  public void setChild(int index, ParseTree tree) {
    tree.setRoot(false);
    try {
      this.children.set(index, tree);
    } catch (IndexOutOfBoundsException e) {
      this.children.add(index, tree);
    }
  }

  /**
   * Get the correct value of this node when it is a leaf or a internal node.
   *
   * @return String with the value of the Leaf or the Symbolic name of the operator
   */
  public String getLeafOrOperator() {
    if (getLeaf() != null) {
      return getLeaf();
    } else if (getEdge() != null) {
      return getEdge().getLabel();
    } else {
      return getSymbolicName() + "[" + children.size() + "]";
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
    return leaf != null || edge != null;
  }

  public void setLeaf(Edge edge) {
    this.edge = edge;
    this.operator = 0;
    this.leaf = null;
  }

  public long getId() {
    return id;
  }

  public boolean isRoot() {
    return root;
  }

  public void setRoot(boolean isRoot) {
    this.root = isRoot;
  }

  /**
   * Deep clone the current parsetree.
   *
   * @return A deep clone of the current parsetree.
   */
  public ParseTree clone() {

    ParseTree clonedTree = null;

    // make a clone of the current tree
    try {
      clonedTree = (ParseTree) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }

    // reset our children
    clonedTree.children = new ArrayList<ParseTree>();

    // recusively clone the left and right childs (parsetrees) so that we don't keep references to the same objects
    for (ParseTree child : this.children) {
      clonedTree.children.add(child.clone());
    }

    return clonedTree;
  }

  /**
   * Post-order treewalk
   *
   * @return Stream of ParseTrees
   */
  public Stream<ParseTree> postOrderTreeWalk() {
    return Stream.concat(
        children.stream().flatMap(ParseTree::postOrderTreeWalk),
        Stream.of(this));
  }

  /**
   * Check if the current tree contains a given operator.
   *
   * @param operator The operator constant (e.g. ParseTree.UNION) to check for containment in the
   *                 tree.
   * @return Boolean indicating if the tree contains the operator.
   */
  public boolean containsOperator(int operator) {
    return postOrderTreeWalk().anyMatch(t -> t.getOperator() == operator);
  }

  public int level() {
    if (this == null)
      return 0;

    if (isLeaf())
      return 1;

    List<Integer> childLevels = new ArrayList<>();
    for (ParseTree child : getChildren()) {
      childLevels.add(child.level());
    }

    return Collections.max(childLevels) + 1;
  }

}
