/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.kpathindex.utilities

import com.github.giedomak.telepath.datamodels.graph.Node
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepath.kpathindex.KPathIndexInMemory
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class KExtenderTest {

    @Test
    fun extendsPathIndex() {
        val kPathIndex = spy<KPathIndexInMemory>()

        // k = 1 paths:
        // 10 1 12
        // 12 2 14
        // 12 2 42
        // 42 3 4
        val path1 = PathIdentifierStore.getPathIdByEdgeLabel("1")
        val path2 = PathIdentifierStore.getPathIdByEdgeLabel("2")
        val path3 = PathIdentifierStore.getPathIdByEdgeLabel("3")

        val paths1 = listOf(Path(path1, listOf(Node("10"), Node("12"))))
        val paths2 = listOf(Path(path2, listOf(Node("12"), Node("14"))), Path(path2, listOf(Node("12"), Node("42"))))
        val paths3 = listOf(Path(path3, listOf(Node("42"), Node("4"))))

        // Insert all k = 1 paths
        paths1.union(paths2).union(paths3).forEach { kPathIndex.insert(it) }

        // Extend from k = 1 to k = 3
        KExtender.run(kPathIndex, 3)

        val path4 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("1", "2"))
        val path5 = PathIdentifierStore.getPathIdByEdgeLabel(listOf("1", "2", "3"))

        // Verify these three paths have been inserted
        verify(kPathIndex).insert(Path(path4, listOf(Node("10"), Node("12"), Node("14"))))
        verify(kPathIndex).insert(Path(path4, listOf(Node("10"), Node("12"), Node("42"))))
        verify(kPathIndex).insert(Path(path5, listOf(Node("10"), Node("12"), Node("42"), Node("4"))))
    }
}
