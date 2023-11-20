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
 *
 */
package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.test.engine.UseCaseEngine;
import org.junit.jupiter.api.Test;

/**
 * Make sure that we are ok with spaces in ID DIVISION
 */
public class TestSpacesInIdDivision {
  private static final String TEXT1 = "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID.PRG.\n"
          + "       AUTHOR.I.SHCHE.\n"
          + "       DATE-WRITTEN.22/06/2018.\n";
  private static final String TEXT2 = "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID. PRG.\n"
          + "       AUTHOR.I.SHCHE.\n"
          + "       DATE-WRITTEN.22/06/2018.\n";
  private static final String TEXT3 = "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID. PRG.\n"
          + "       DATE-WRITTEN.22/06/2018.\n";

  @Test
  void test1() {
    UseCaseEngine.runTest(TEXT1, ImmutableList.of(), ImmutableMap.of());
  }

  @Test
  void test2() {
    UseCaseEngine.runTest(TEXT2, ImmutableList.of(), ImmutableMap.of());
  }

  @Test
  void test3() {
    UseCaseEngine.runTest(TEXT3, ImmutableList.of(), ImmutableMap.of());
  }
}
