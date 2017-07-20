/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.utilities;

import com.telepathdb.datamodels.ParseTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to print ParseTrees
 * <p>
 * Example:
 * <p>
 * Your input: a/(b+|c)|d
 * <p>
 * UNION
 * / \
 * /   \
 * /     \
 * /       \
 * /         \
 * /           \
 * /             \
 * /               \
 * CONCATENATION               d
 * / \
 * /   \
 * /     \
 * /       \
 * a       UNION
 * / \
 * /   \
 * PLUS   c
 * /
 * b
 */
public class ParseTreePrinter {

  /**
   * Static method we should call with the root parseTree as parameter in order to print it.
   *
   * @param root The root of the ParseTree we want to print
   */
  public static void printParseTree(ParseTree root) {
    int maxLevel = ParseTreePrinter.maxLevel(root);

    Logger.debug("", false);
    printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    Logger.debug("", true);

  }

  private static void printNodeInternal(List<ParseTree> nodes, int level, int maxLevel) {
    if (nodes.isEmpty() || ParseTreePrinter.isAllElementsNull(nodes))
      return;

    int floor = maxLevel - level;
    int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
    int firstSpaces = (int) Math.pow(2, (floor)) - 1;
    int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

    ParseTreePrinter.printWhitespaces(firstSpaces);

    List<ParseTree> newNodes = new ArrayList<ParseTree>();
    for (ParseTree node : nodes) {
      if (node != null) {
        System.out.print(node.getLeafOrOperator());
        newNodes.add(node.getChild(0));
        newNodes.add(node.getChild(1));
        newNodes.add(node.getChild(2));
      } else {
        newNodes.add(null);
        newNodes.add(null);
        System.out.print(" ");
      }

      ParseTreePrinter.printWhitespaces(betweenSpaces);
    }

    System.out.println("");
    Logger.debug("", false);

    for (int i = 1; i <= endgeLines; i++) {
      for (int j = 0; j < nodes.size(); j++) {
        ParseTreePrinter.printWhitespaces(firstSpaces - i);
        if (nodes.get(j) == null) {
          ParseTreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
          continue;
        }

        if (nodes.get(j).hasChild(0))
          System.out.print("/");
        else
          ParseTreePrinter.printWhitespaces(1);

        ParseTreePrinter.printWhitespaces(i + i - 1);

        if (nodes.get(j).hasChild(1))
          System.out.print("\\");
        else
          ParseTreePrinter.printWhitespaces(1);

        ParseTreePrinter.printWhitespaces(i + i - 1);

        if (nodes.get(j).hasChild(2))
          System.out.print("\\");
        else
          ParseTreePrinter.printWhitespaces(1);

        ParseTreePrinter.printWhitespaces(endgeLines + endgeLines - i);
      }

      System.out.println("");
      Logger.debug("", false);
    }

    printNodeInternal(newNodes, level + 1, maxLevel);
  }

  private static void printWhitespaces(int count) {
    for (int i = 0; i < count; i++)
      System.out.print(" ");
  }

  private static int maxLevel(ParseTree node) {
    if (node == null)
      return 0;

    if (node.isLeaf())
      return 1;

    List<Integer> childLevels = new ArrayList<>();
    for (ParseTree child : node.getChildren()) {
      childLevels.add(ParseTreePrinter.maxLevel(child));
    }

    return Collections.max(childLevels) + 1;
  }

  private static <T> boolean isAllElementsNull(List<T> list) {
    for (Object object : list) {
      if (object != null)
        return false;
    }

    return true;
  }
}
