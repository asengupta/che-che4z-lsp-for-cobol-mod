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
 *
 */
package org.eclipse.lsp.cobol.core;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.eclipse.lsp.cobol.core.hw.Diagnostic;

import java.util.List;

/**
 * Parses IDMS Panel Definition files
 */
public class AntlrIdmsPanelDefinitionParser {
    private final CommonTokenStream tokens;
    private final PanelDefinitionParser antlrParser;

    public AntlrIdmsPanelDefinitionParser(CharStream input, BaseErrorListener listener, DefaultErrorStrategy errorStrategy, ParseTreeListener treeListener) {
        PanelDefinitionLexer antlrLexer = new PanelDefinitionLexer(input);
        antlrLexer.removeErrorListeners();
        tokens = new CommonTokenStream(antlrLexer);
        antlrLexer.addErrorListener(listener);
        antlrParser = new PanelDefinitionParser(tokens);
        antlrParser.removeErrorListeners();
        antlrParser.addErrorListener(listener);
        antlrParser.setErrorHandler(errorStrategy);
        antlrParser.addParseListener(treeListener);
    }

    /**
     * Runs the parser
     * @return StartRuleContext
     */
    public PanelDefinitionParser.StartRuleContext runParser() {
        return antlrParser.startRule();
    }

    /**
     * Gets tokens
     * @return CommonTokenStream
     */
    public CommonTokenStream getTokens() {
        return tokens;
    }

    /**
     * Creates diagnostics
     * @return List<Diagnostic>
     */
    public List<Diagnostic> diagnostics() {
        return ImmutableList.of();
    }
}
