package com.github.giedomak.telepath.kpathindex

import com.github.giedomak.telepath.datamodels.PathTest
import com.github.giedomak.telepath.datamodels.graph.Path
import com.github.giedomak.telepath.datamodels.graph.PathPrefix
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import kotlin.streams.toList
import kotlin.test.assertEquals

class KPathIndexDiskTest {

    private val expected = listOf(
            Path(42, PathTest.equalNodes(3, "42")),
            Path(43, PathTest.equalNodes(2, "48"))
    )
    private var kPathIndex = mock<KPathIndexDisk> {
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
