/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepath project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepath.utilities

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class LoggerTest {

    // Need to test console output (System.out)
    private val outContent = ByteArrayOutputStream()
    private val stdout = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
    }

    @After
    fun cleanUpStreams() {
        System.setOut(stdout)
    }

    @Test
    fun printsWithCallerNameAndPadding() {

        Logger.debug("YAY1!")
        Logger.info("YAY2!")
        Logger.warning("YAY3!")
        Logger.error("YAY4!")
        Logger.fatal("YAY5!")

        for (i in 1..5) {
            assertThat(outContent.toString(), containsString("         LoggerTest: YAY$i!\n"))
        }
    }

    @Test
    fun printsNoNewline() {

        Logger.debug("YAY1!", false)

        assertThat(outContent.toString(), not(containsString("\n")))
    }
}
