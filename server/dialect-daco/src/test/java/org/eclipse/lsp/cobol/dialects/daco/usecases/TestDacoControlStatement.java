/*
 * Copyright (c) 2021 Broadcom.
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
package org.eclipse.lsp.cobol.dialects.daco.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.common.error.ErrorSource;
import org.eclipse.lsp.cobol.dialects.daco.utils.DialectConfigs;
import org.eclipse.lsp.cobol.test.engine.UseCaseEngine;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test DACO-CONTROL statement recognition
 */
class TestDacoControlStatement {

  private static final String TEXT =
          "        IDENTIFICATION DIVISION. \r\n"
                  + "        PROGRAM-ID. test1. \r\n"
                  + "ENVIRONMENT  DIVISION.\n"
                  + "DACO-CONTROL SECTION.\n"
                  + "    ROW BUFFER TBLLAY-XW4 IS YES.\n"
                  + "IDMS-CONTROL SECTION.\n"
                  + "PROTOCOL.    MODE IS BATCH DEBUG\n"
                  + "             IDMS-RECORDS MANUAL.";

  @Test
  void test() {
    UseCaseEngine.runTest(
            TEXT, ImmutableList.of(), ImmutableMap.of(), ImmutableList.of(), DialectConfigs.getDaCoAnalysisConfig());
  }
}
