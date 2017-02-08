/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.memorymanager;

/**
 * Simple class used to estimate memory usage.
 */
public final class StringSizeEstimator {

  private static int OBJ_HEADER;
  private static int ARR_HEADER;
  private static int INT_FIELDS = 12;
  private static int OBJ_REF;
  private static int OBJ_OVERHEAD;
  private static boolean IS_64_BIT_JVM;

  /**
   * Private constructor to prevent instantiation.
   */
  private StringSizeEstimator() {
  }

  /**
   * Class initializations.
   */
  static {
    // By default we assume 64 bit JVM
    // (defensive approach since we will get
    // larger estimations in case we are not sure)
    IS_64_BIT_JVM = true;
    // check the system property "sun.arch.data.model"
    // not very safe, as it might not work for all JVM implementations
    // nevertheless the worst thing that might happen is that the JVM is 32bit
    // but we assume its 64bit, so we will be counting a few extra bytes per string object
    // no harm done here since this is just an approximation.
    String arch = System.getProperty("sun.arch.data.model");
    if (arch != null) {
      if (arch.contains("32")) {
        // If exists and is 32 bit then we assume a 32bit JVM
        IS_64_BIT_JVM = false;
      }
    }
    // The sizes below are a bit rough as we don't take into account
    // advanced JVM options such as compressed oops
    // however if our calculation is not accurate it'll be a bit over
    // so there is no danger of an out of memory error because of this.
    OBJ_HEADER = IS_64_BIT_JVM ? 16 : 8;
    ARR_HEADER = IS_64_BIT_JVM ? 24 : 12;
    OBJ_REF = IS_64_BIT_JVM ? 8 : 4;
    OBJ_OVERHEAD = OBJ_HEADER + INT_FIELDS + OBJ_REF + ARR_HEADER;

  }

  /**
   * Estimates the size of a {@link String} object in bytes.
   *
   * This function was designed with the following goals in mind (in order of importance) :
   *
   * First goal is speed: this function is called repeatedly and it should
   * execute in not much more than a nanosecond.
   *
   * Second goal is to never underestimate (as it would lead to memory shortage and a crash).
   *
   * Third goal is to never overestimate too much (say within a factor of two), as it would
   * mean that we are leaving much of the RAM underutilized.
   *
   * @param s The string to estimate memory footprint.
   * @return The <strong>estimated</strong> size in bytes.
   */
  public static long estimatedSizeOf(String s) {
    return (s.length() * 2) + OBJ_OVERHEAD;
  }

}