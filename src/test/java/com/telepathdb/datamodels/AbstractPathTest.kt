package com.telepathdb.datamodels

import com.telepathdb.datamodels.PathPrefixTest.simplePathPrefix
import com.telepathdb.datamodels.PathTest.Companion.simplePath
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class AbstractPathTest {

    @Test
    fun prefixLessThanLongerPath() {
        // given
        val prefix = simplePathPrefix(42, 4, 2, 1L)
        val path = simplePath(42, 4, 1L)

        // then
        assertThat<AbstractPath>(path, greaterThan(prefix))
    }

    @Test
    fun prefixEqualToPath() {
        // given
        val prefix = simplePathPrefix(42, 4, 4, 2L)
        val path = simplePath(42, 4, 2L)

        // then
        assertThat<AbstractPath>(prefix, comparesEqualTo(path))
    }

    @Test
    fun prefixLessThanPath() {
        // given
        val prefix = simplePathPrefix(42, 4, 2, 2L)
        val path = simplePath(42, 4, 3L)

        // then
        assertThat<AbstractPath>(prefix, lessThan(path))
    }
}
