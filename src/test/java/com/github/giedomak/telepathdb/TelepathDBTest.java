/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb;

import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TelepathDBTest {

  // Need to test console output (System.out)
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void cleanUpStreams() {
    System.setOut(null);
    System.setErr(null);
  }

  public void testScanner() {
    // Inspiration: http://stackoverflow.com/a/34139918/3238444
  }

//   @Test
//   public void mainMethodPrintsUpAndRunningString() {
//     TelepathDB telepathDB = mock( TelepathDB.class );
//     MemoryManager memoryManager = mock( MemoryManager.class );
//     Logger logger = mock( Logger.class);
//
//     doNothing().doThrow(new TestException()).when( logger ).info(notNull());
//
//     try {
//       telepathDB.main(null);
//     } catch(TestException e) {}
//
//     assertEquals(outContent, "yay");
//   }

  // We are using this exception to break out of a while(true) loop
  // Inspired by: http://stackoverflow.com/a/30059971/3238444
  @SuppressWarnings("serial")
  private class TestException extends RuntimeException {
  }
}
