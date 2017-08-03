/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.memorymanager;

import com.github.giedomak.telepathdb.datamodels.PathTest
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class MemoryManagerTest {

    @Test
    fun writesAndReadsPartitionsToDisk() {

        val memoryManager = spy<MemoryManager>()
        doReturn(false).whenever(memoryManager).fitsIntoMemory(any())

        val expected = listOf(PathTest.simplePath(42, 3, 44))

        // Add our expected path to the MemoryManager, this should write it to disk since it does not fitsIntoMemory.
        val id = memoryManager.add(expected.stream())
        verify(memoryManager).writePartition(any(), any())

        // Retrieving it will collect the path from disk.
        val actual = memoryManager[id].toList()

        assertEquals(expected, actual)
    }
}
