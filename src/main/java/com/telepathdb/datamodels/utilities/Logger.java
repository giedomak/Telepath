/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.utilities;

/**
 * This Logger class is reponsible for logging in 5 levels:
 * debug, info, warning, error and fatal
 */
public final class Logger {

  public static void debug(Object object) {
    log(object.toString());
  }

  public static void debug(Object object, boolean println) {
    log(object.toString(), println);
  }

  public static void info(Object text) {
    log(text.toString());
  }

  public static void warning(Object object) {
    log(object.toString());
  }

  public static void error(Object object) {
    log(object.toString());
  }

  public static void fatal(Object object) {
    log(object.toString());
  }

  private static void log(String text) {
    log(text, true);
  }

  /**
   * This wil log a statement to the console for now
   *
   * @param text    The text to print
   * @param println Boolean indicating if we want to print on a newline or not
   */
  private static void log(String text, boolean println) {
    if (println) {
      System.out.println(padLeft(getCallerClassName(), 19) + ": " + text);
    } else {
      System.out.print(padLeft(getCallerClassName(), 19) + ": " + text);
    }
  }

  /**
   * Get the Caller's ClassName, so we can add that in our log
   *
   * @return A string with the ClassName of the caller, with the packages stripped away.
   */
  private static String getCallerClassName() {
    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    for (int i = 1; i < stElements.length; i++) {
      StackTraceElement ste = stElements[i];
      String name = ste.getClassName();
      if (!name.equals(Logger.class.getName()) &&
          name.indexOf("java.lang.Thread") != 0 &&
          name.indexOf("Lambda") == -1 &&
          name.indexOf("ArrayList") == -1 &&
          name.indexOf("ForEachOps") == -1 &&
          name.indexOf("SliceOps") == -1 &&
          name.indexOf("Pipeline") == -1) {
        return name.substring(name.lastIndexOf('.') + 1);
      }
    }
    return null;
  }

  /**
   * Pad a String with spaces on the right
   *
   * @param string The String to pad
   * @param n      Number of total chars
   * @return The padded String
   */
  private static String padRight(String string, int n) {
    return String.format("%1$-" + n + "s", string);
  }

  /**
   * Pad a String with spaces from the left
   *
   * @param string The String to pad
   * @param n      Number of total chars
   * @return The padded String
   */
  private static String padLeft(String string, int n) {
    return String.format("%1$" + n + "s", string);
  }
}
