/*
 * Copyright (c) 2024 Broadcom.
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
package org.eclipse.lsp.cobol.core;

import org.eclipse.lsp.cobol.core.hw.CobolLexer;
import org.eclipse.lsp.cobol.core.hw.CobolParser;
import org.eclipse.lsp.cobol.core.hw.ParseResult;
import org.eclipse.lsp.cobol.core.hw.ParserSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HwSkipStatementsTest {
  @Test
  void testSkipStatements() {
    final String source =
            "        ID DIVISION. PROGRAM-ID Pr1.\n"
                    + "        SKIP2.\n"
                    + "        END PROGRAM Pr1.\n"
                    + "        SKIP1\n"
                    + "        ID DIVISION. PROGRAM-ID. Pr2.\n"
                    + "        SKIP3.\n"
                    + "        END PROGRAM Pr2.\n";
    ParseResult result = new CobolParser(new CobolLexer(source), new ParserSettings()).parse();
    assertEquals(0, result.getDiagnostics().size());
    assertEquals(source, result.getSourceUnit().toText());
  }
}
