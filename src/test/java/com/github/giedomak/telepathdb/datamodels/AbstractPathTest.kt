package com.github.giedomak.telepathdb.datamodels

import com.github.giedomak.telepathdb.datamodels.graph.AbstractPath
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class AbstractPathTest {

    @Test
    fun prefixLessThanLongerPath() {
        // given
        val prefix = PathPrefixTest.simplePathPrefix(42, 4, 2, 1L)
        val path = PathTest.simplePath(42, 4, 1L)

        // then
        assertThat<AbstractPath>(path, greaterThan(prefix))
    }

    @Test
    fun prefixEqualToPath() {
        // given
        val prefix = PathPrefixTest.simplePathPrefix(42, 4, 4, 2L)
        val path = PathTest.simplePath(42, 4, 2L)

        // then
        assertThat<AbstractPath>(prefix, comparesEqualTo(path))
    }

    @Test
    fun prefixLessThanPath() {
        // given
        val prefix = PathPrefixTest.simplePathPrefix(42, 4, 2, 2L)
        val path = PathTest.simplePath(42, 4, 3L)

        // then
        assertThat<AbstractPath>(prefix, lessThan(path))
    }
}
