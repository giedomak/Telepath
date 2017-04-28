package com.telepathdb.staticparser;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.utilities.ParseTreePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Split a ParseTree on Union into multiple ParseTrees
 * Illustrative example: 'a/(b|c)/d' becomes ['a/b/d', 'a/c/d']
 */
final public class UnionPuller {

  /**
   * Removes the UNION operator from a ParseTree.
   *
   * Simply duplicate the tree and on the position of the UNION operator, the first tree
   * gets the left-child and the other tree gets the right-child.
   *
   * @param parseTree The ParseTree to parse.
   * @return List of ParseTrees each not containing the UNION operator.
   */
  public static List<ParseTree> parse(ParseTree parseTree) {

    List<ParseTree> parseTrees = new ArrayList<ParseTree>();
    parseTrees.add(parseTree);

    // Print the parsed ParseTree
    ParseTreePrinter.printParseTree(parseTree);

    // List to hold the ParseTrees which UNIONS in them
    List<ParseTree> unionTrees;

    // Collect the ParseTrees which contain the UNION operator
    while ((unionTrees = parseTrees.stream().filter(t -> t.containsOperator(ParseTree.UNION)).collect(Collectors.toList())).size() > 0) {

      // For each of those trees with the UNION operator in them
      for (ParseTree tree : unionTrees) {

        // Split them immediately when the Root is the UNION operator
        if (tree.isRoot() && tree.getOperator() == ParseTree.UNION) {

          // Remove the current tree from the list, and add its left child and right child
          parseTrees.remove(tree);
          parseTrees.add(tree.getLeft().clone().setRoot());
          parseTrees.add(tree.getRight().clone().setRoot());
          continue; // Continue to the next parsetree containing UNION
        }

        // Deep clone the current tree
        ParseTree clone = tree.clone();

        // Recursively remove the first UNION we find doing a pre-order treewalk.
        // Replace the UNION node with its left child in the original tree, and with the
        // right child in the clone of the original tree.
        RemoveFirstUnion(tree, "left");
        RemoveFirstUnion(clone, "right");

        // We still have to add the clone to the list
        parseTrees.add(clone);
      }
    }

    return parseTrees;
  }

  /**
   * Recusively replace UNION nodes of parsetrees with its child, chosen by the param childChooser.
   * We use a pre-order tree walk and return after we've replaced the first UNION with its child.
   *
   * @param tree The tree we have to traverse finding the first occurence of a UNION operator.
   * @param childChooser Define if we have to replace the UNION node with its right or left child.
   * @return Boolean indicating if we've replaced a UNION node.
   */
  private static boolean RemoveFirstUnion(ParseTree tree, String childChooser) {

    // Return if we've reached a leaf
    if (tree.isLeaf()) {
      return false;
    }

    // Check if our left child is a UNION node. If so, replace it with the chosen child of our left child.
    if (tree.hasLeft() && tree.getLeft().getOperator() == ParseTree.UNION) {
      if (childChooser == "left") {
        tree.setLeft(tree.getLeft().getLeft());
      } else if (childChooser == "right") {
        tree.setLeft(tree.getLeft().getRight());
      }
      // Return if we've found one, breaking the recursive call
      return true;
    }

    // Check if our right child is a UNION node. If so, replace it with the chosen child of our right child.
    if (tree.hasRight() && tree.getRight().getOperator() == ParseTree.UNION) {
      if (childChooser == "left") {
        tree.setRight(tree.getRight().getLeft());
      } else if (childChooser == "right") {
        tree.setRight(tree.getRight().getRight());
      }
      // Return if we've found one, breaking the recursive call
      return true;
    }

    // Traverse to the left child if we haven't found a UNION node already
    if (tree.hasLeft()) {
      if(RemoveFirstUnion(tree.getLeft(), childChooser)) {
        return true;
      }
    }
    // Traverse to the right child if we haven't found a UNION node already
    if (tree.hasRight()) {
      if(RemoveFirstUnion(tree.getRight(), childChooser)) {
        return true;
      }
    }

    return false;
  }
}
