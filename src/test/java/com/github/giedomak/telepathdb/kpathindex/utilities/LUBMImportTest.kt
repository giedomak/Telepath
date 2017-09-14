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

class LUBMImportTest {

    @Test
    fun importsStuff() {
        val kPathIndex = mock<KPathIndexInMemory>()
        val importedLines = LUBMImport.run(kPathIndex, "src/test/resources/universities.nt")

        assertEquals(49, importedLines)
    }

    @Test
    fun importsStuffInGMarkFormatAndIsSearchable() {
        val kPathIndex = mock<KPathIndexInMemory>()
        LUBMImport.run(kPathIndex, "src/test/resources/universities.nt")

        // The path identifier for edge `worksFor`
        val pathId = PathIdentifierStore.getPathIdByEdgeLabel("worksFor")

        // Check if there are 100 insertions
        verify(kPathIndex, times(98)).insert(any())

        // Check if the first and last path are inserted
        verify(kPathIndex).insert(Path(pathId, listOf(Node("http://www.Department0.University0.edu/FullProfessor3"), Node("http://www.Department0.University0.edu"))))
        verify(kPathIndex).insert(Path(pathId, listOf(Node("http://www.Department0.University0.edu/FullProfessor0"), Node("http://www.Department0.University0.edu"))))
    }
}