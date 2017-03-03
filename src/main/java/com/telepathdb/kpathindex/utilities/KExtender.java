/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.kpathindex.utilities;

import com.telepathdb.datamodels.Path;
import com.telepathdb.datamodels.PathPrefix;
import com.telepathdb.datamodels.stores.PathIdentifierStore;
import com.telepathdb.kpathindex.KPathIndex;
import com.telepathdb.physicallibrary.PhysicalLibrary;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by giedomak on 23/02/2017.
 */
public final class KExtender {

  static public long run(KPathIndex kPathIndex, int k) {

    // Return if our kPathIndex is already of size k
    if (kPathIndex.getK() >= k) return 0;

    System.out.println("KExtender: current k: " + kPathIndex.getK() + ", target: " + k);

    // Current K of kPathIndex
    // FlatMaps the Stream of Streams<Path> into one 'flattened' Stream<Path>
    Stream<Path> source_k = PathIdentifierStore.getPathIdentifiers(kPathIndex.getK())
        .stream()
        .flatMap(id -> kPathIndex.search(new PathPrefix(id)));

    // K = 1
    Stream<Path> k1 = PathIdentifierStore.getPathIdentifiers(1)
        .stream()
        .flatMap(id -> kPathIndex.search(new PathPrefix(id)));

    // Concatenate the current K paths, with the K=1 paths so we get the K=K+1 paths
    List<Path> paths = PhysicalLibrary.concatenation(source_k, k1).collect(Collectors.toList());
    System.out.println("KExtender: concatenation done: " + paths.size());

    // Make sure we insert after we collected the results, otherwise we get a concurrency exception
    // because we are inserting while we haven't consumed the whole stream yet.
    paths.forEach(kPathIndex::insert);

    // Set K to K+1
    kPathIndex.setK(kPathIndex.getK() + 1);

    // Recusive call until we reach our target k
    long size = paths.size() + run(kPathIndex, k);

    return size;
  }
}
