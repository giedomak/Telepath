/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.kpathindex

import com.github.giedomak.telepathdb.datamodels.PathTest
import com.github.giedomak.telepathdb.datamodels.graph.Path
import com.github.giedomak.telepathdb.datamodels.graph.PathPrefix
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class KPathIndexInMemoryTest {

    private val expected = listOf(
            Path(42, PathTest.equalNodes(3, "42")),
            Path(43, PathTest.equalNodes(2, "48"))
    )
    private var kPathIndex = mock<KPathIndexInMemory> {
        on { search(PathPrefix(42)) }.doReturn(expected.stream())
    }

    // ---------- SEARCH -----------

    @Test
    fun searchMethod() {
        val actual = kPathIndex.search(PathPrefix(42)).toList()

        assertEquals(expected, actual)
    }

    // ---------- INSERT -----------

    @Test
    fun insertMethod() {
        val path = mock<Path>()
        kPathIndex.insert(path)
        // Make sure the insert method was called with the right argument
        verify(kPathIndex).insert(path)
    }
}
