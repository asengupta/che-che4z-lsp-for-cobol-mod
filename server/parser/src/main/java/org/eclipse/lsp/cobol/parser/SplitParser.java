/*
 * Copyright (c) 2023 Broadcom.
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
package org.eclipse.lsp.cobol.parser;

import lombok.Getter;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.eclipse.lsp.cobol.common.utils.ThreadInterruptionUtil;
import org.eclipse.lsp.cobol.core.CobolLexer;
import org.eclipse.lsp.cobol.core.CobolParser;
import org.eclipse.lsp.cobol.cst.CstBuilderVisitor;
import org.eclipse.lsp.cobol.cst.nodes.CompilationUnit;

/**
 * COBOL parser class.
 */
public class SplitParser implements CstBuilder {
  @Getter
  private final CommonTokenStream tokens;
  private final CobolParser antlrParser;

  public SplitParser(CharStream input, BaseErrorListener listener, DefaultErrorStrategy errorStrategy, ParseTreeListener treeListener) {
    CobolLexer antlrLexer = new CobolLexer(input);
    antlrLexer.removeErrorListeners();
    antlrLexer.addErrorListener(listener);
    tokens = new CommonTokenStream(antlrLexer);
    antlrParser = new CobolParser(tokens);
    antlrParser.removeErrorListeners();
    antlrParser.addErrorListener(listener);
    antlrParser.setErrorHandler(errorStrategy);
    antlrParser.addParseListener(treeListener);
  }

  @Override
  public CompilationUnit runParser() {
    ThreadInterruptionUtil.checkThreadInterrupted();
    return new CstBuilderVisitor().visit(antlrParser.startRule());
  }
}
