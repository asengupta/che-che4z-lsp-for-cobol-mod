/*
 * Copyright (c) 2020 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *    DAF Trucks NV – implementation of DaCo COBOL statements
 *    and DAF development standards
 *
 */

import org.junit.jupiter.api.Test;

/**
 * Tests fot SplittingParser
 */
public class SplitSourceByPrograms {
  private final static String NESTED_PROGRAM = "IDENTIFICATION DIVISION.\n" +
          "PROGRAM-ID. W...\n" +
          "PROCEDURE DIVISION...\n" +
          "CALL Z...\n" +
          "CALL Y USING DFHEIBLK COMMAREA...\n" +
          "CALL X USING DFHEIBLK COMMAREA...\n" +
          "IDENTIFICATION DIVISION.\n" +
          "PROGRAM-ID. X...\n" +
          "PROCEDURE DIVISION USING DFHEIBLK DFHCOMMAREA..\n" +
          "CALL Z...\n" +
          "CALL Y USING DFHEIBLK COMMAREA...\n" +
          "END PROGRAM X.\n" +
          "IDENTIFICATION DIVISION.\n" +
          "PROGRAM-ID. Y IS COMMON...\n" +
          "PROCEDURE DIVISION USING DFHEIBLK DFHCOMMAREA...\n" +
          "CALL Z...\n" +
          "EXEC CICS.....\n" +
          "END PROGRAM Y.\n" +
          "IDENTIFICATION DIVISION.\n" +
          "PROGRAM-ID. Z IS COMMON...\n" +
          "PROCEDURE DIVISION...\n" +
          "END PROGRAM Z.\n" +
          "END PROGRAM W.";

  @Test
  void nestedPrograms() {
    System.out.println(NESTED_PROGRAM);
  }
}
