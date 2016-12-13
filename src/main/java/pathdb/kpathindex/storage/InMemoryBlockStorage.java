/**
 * Copyright (C) 2015-2016 - All rights reserved.
 * This file is part of the PathDB project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package pathdb.kpathindex.storage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBlockStorage {

  private final long maxPageSize;
  private Map<Integer, byte[]> storage;

  public InMemoryBlockStorage(long maxPageSize) {
    this.maxPageSize = maxPageSize;
    storage = new HashMap<>();
  }

  public void writeBytes(int start, int end, byte[] bytes) {
    if (bytes.length < maxPageSize) {
      storage.put(start, Arrays.copyOf(bytes, end));
    } else {
      throw new IllegalArgumentException("Attempted to write byte array larger than page size.");
    }
  }

  public byte[] getBytes(long location) {
    if (storage.containsKey(location)) {
      return storage.get(location);
    } else {
      throw new IllegalArgumentException("Attempted to retrieve bytes from uninitialized location.");
    }
  }
}
