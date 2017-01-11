/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.kpathindex;

/**
 * This KPathIndexFactory will be used to abstract away if we use the InMemory or DiskBased
 * KPathIndex
 */
public class KPathIndexFactory {

  private static KPathIndex kPathIndex;

  /**
   * For now we are using the InMemory KPathIndex, which we have defined here in this factory
   *
   * @return The kPathIndex
   */
  public static KPathIndex getKPathIndex() {

    if (kPathIndex == null) {
      kPathIndex = new KPathIndexInMemory();
    }
    return kPathIndex;

  }
}
