/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.memorymanager

import com.github.giedomak.telepathdb.datamodels.PathTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.streams.toList
import kotlin.test.assertEquals

class MemoryManagerTest {

    private val logger = ByteArrayOutputStream()
    private val stdout = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(logger))
    }

    @After
    fun cleanUpStreams() {
        System.setOut(stdout)
    }

    @Test
    fun writesAndReadsPartitionsToDisk() {

        val memoryManager = spy<MemoryManager>()
        doReturn(false).whenever(memoryManager).fitsIntoMemory(any())

        val expected = listOf(
                PathTest.simplePath(42, 3, 44),
                PathTest.simplePath(47, 4, 40)
        )

        // Add our expected path to the MemoryManager, this should write it to disk since it does not fitsIntoMemory.
        val id = memoryManager.add(expected.stream())

        // Verify the Logger has written what we expected
        assertThat(logger.toString(), containsString("Partition written"))

        // Retrieving it will collect the path from disk.
        val actual = memoryManager[id].toList()

        assertEquals(expected, actual)
    }
}
