/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.kpathindex.utilities

import com.github.giedomak.telepathdb.datamodels.graph.Node
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.stores.PathIdentifierStore
import com.github.giedomak.telepathdb.kpathindex.KPathIndexInMemory
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import kotlin.test.assertEquals

class GMarkImportTest {

    @Test
    fun importsStuffInGMarkFormat() {
        val kPathIndex = mock<KPathIndexInMemory>()
        val importedLines = GMarkImport.run(kPathIndex, "src/test/resources/graph10K.txt")

        assertEquals(100, importedLines)
    }

    @Test
    fun importsStuffInGMarkFormatAndIsSearchable() {
        val kPathIndex = mock<KPathIndexInMemory>()
        GMarkImport.run(kPathIndex, "src/test/resources/graph10K.txt")

        // The path identifier for edge `1`
        val pathId = PathIdentifierStore.getPathIdByEdgeLabel("1")

        // Check if there are 100 insertions
        verify(kPathIndex, times(100)).insert(any())

        // Check if the first and last path are inserted
        verify(kPathIndex).insert(Path(pathId, listOf(Node(10), Node(12))))
        verify(kPathIndex).insert(Path(pathId, listOf(Node(2514), Node(3107))))
    }
}
