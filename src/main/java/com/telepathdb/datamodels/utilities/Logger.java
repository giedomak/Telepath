package com.telepathdb.datamodels.utilities;

/**
 * Created by giedomak on 03/03/2017.
 */
public final class Logger {

  public static void debug(String text) {
    log(text);
  }

  public static void debug(String text, boolean println) {
    log(text, println);
  }

  public static void info(Object text) {
    log(text.toString());
  }

  public static void warning(String text) {
    log(text);
  }

  public static void error(String text) {
    log(text);
  }

  public static void fatal(String text) {
    log(text);
  }

  private static void log(String text) {
    log(text, true);
  }

  private static void log(String text, boolean println) {
    if (println) {
      System.out.println(padLeft(getCallerClassName(), 19) + ": " + text);
    } else {
      System.out.print(padLeft(getCallerClassName(), 19) + ": " + text);
    }
  }

  private static String getCallerClassName() {
    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    for (int i = 1; i < stElements.length; i++) {
      StackTraceElement ste = stElements[i];
      String name = ste.getClassName();
      if (!name.equals(Logger.class.getName()) &&
          name.indexOf("java.lang.Thread") != 0 &&
          name.indexOf("Lambda") == -1 &&
          name.indexOf("ArrayList") == -1) {
        return name.substring(name.lastIndexOf('.') + 1);
      }
    }
    return null;
  }

  private static String padRight(String string, int n) {
    return String.format("%1$-" + n + "s", string);
  }

  private static String padLeft(String string, int n) {
    return String.format("%1$" + n + "s", string);
  }
}
