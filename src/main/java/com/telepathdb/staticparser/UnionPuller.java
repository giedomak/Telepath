/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.staticparser;

import com.telepathdb.datamodels.ParseTree;
import com.telepathdb.datamodels.utilities.Logger;
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
   * Removes the UNION operatorId from a ParseTree.
   * <p>
   * Simply duplicate the tree and on the position of the UNION operatorId, the first tree
   * gets the left-child and the other tree gets the right-child.
   *
   * @param parseTree The ParseTree to parse.
   * @return List of ParseTrees each not containing the UNION operatorId.
   */
  public static List<ParseTree> parse(ParseTree parseTree) {

    List<ParseTree> parseTrees = new ArrayList<ParseTree>();
    parseTrees.add(parseTree);

    // Print the parsed ParseTree
    ParseTreePrinter.printParseTree(parseTree);

    // List to hold the ParseTrees which UNIONS in them
    List<ParseTree> unionTrees;

    // Collect the ParseTrees which contain the UNION operatorId
    while ((unionTrees = parseTrees.stream().filter(t -> t.containsOperator(ParseTree.UNION)).collect(Collectors.toList())).size() > 0) {

      // For each of those trees with the UNION operatorId in them
      for (ParseTree tree : unionTrees) {

        // Split them immediately when the Root is the UNION operatorId
        if (tree.isRoot() && tree.getOperatorId() == ParseTree.UNION) {

          // Remove the current tree from the list, and add its children to our parseTrees var
          parseTrees.remove(tree);
          for (ParseTree child : tree.getChildren()) {
            ParseTree cloned = child.clone();
            cloned.setRoot(true); // This cloned tree is now a root
            parseTrees.add(cloned);
          }
          continue; // Continue to the next parsetree containing UNION
        }

        // Deep clone the current tree
        ParseTree clone = tree.clone();

        // Recursively remove the first UNION we find doing a pre-order treewalk.
        // Replace the UNION node with its left child in the original tree, and with the
        // right child in the clone of the original tree.
        removeFirstUnion(tree, 0);
        removeFirstUnion(clone, 1);

        Logger.INSTANCE.debug("UNIONNNN");
        ParseTreePrinter.printParseTree(tree);
        ParseTreePrinter.printParseTree(clone);

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
   * @param tree              The tree we have to traverse finding the first occurence of a UNION operatorId.
   * @param childChooserIndex Define if we have to replace the UNION node with its right or left child.
   * @return Boolean indicating if we've replaced a UNION node.
   */
  private static boolean removeFirstUnion(ParseTree tree, int childChooserIndex) {

    // Return if we've reached a leaf
    if (tree.isLeaf()) {
      return false;
    }

    // Check each child for a UNION node.
    for (int i = 0; i < tree.getChildren().size(); i++) {
      ParseTree child = tree.getChild(i);
      // Check if our child is a UNION node. If so, replace it with the childChooserIndex of our child.
      if (child.getOperatorId() == ParseTree.UNION) {
        tree.setChild(i, child.getChild(childChooserIndex));
        // Return if we've found one, breaking the recursive call
        return true;
      }
    }

    // Traverse to the left child if we haven't found a UNION node already
    for (ParseTree child : tree.getChildren()) {
      if (removeFirstUnion(child, childChooserIndex)) {
        return true;
      }
    }

    return false;
  }
}
