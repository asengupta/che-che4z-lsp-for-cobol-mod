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

package org.eclipse.lsp.cobol.core.model.tree;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.eclipse.lsp.cobol.common.EmbeddedLanguage;
import org.eclipse.lsp.cobol.common.model.Locality;
import org.eclipse.lsp.cobol.common.model.tree.Node;
import org.eclipse.lsp.cobol.core.engine.OldMapping;
import org.eclipse.lsp.cobol.core.visitor.CICSVisitor;
import org.eclipse.lsp.cobol.core.visitor.Db2SqlVisitor;

import java.util.List;

import static org.eclipse.lsp.cobol.common.model.NodeType.EMBEDDED_CODE;

/** This class represents embedded code parts in COBOL program, e.g. EXEC CICS and EXEC SQL */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmbeddedCodeNode extends Node {
  TokenStream tokens;
  ParserRuleContext tree;
  EmbeddedLanguage lang;

  public EmbeddedCodeNode(
          Locality location, TokenStream tokens, ParserRuleContext tree, EmbeddedLanguage lang) {
    super(location, EMBEDDED_CODE);
    this.tokens = tokens;
    this.tree = tree;
    this.lang = lang;
  }

  /**
   * Apply language-specific visitor to the node content and replace it with the actual semantic
   * content of the code part.
   *
   * @param mapping a map with actual token localities
   */
  public void analyzeTree(OldMapping mapping) {
    getParent().removeChild(this);
    instanceVisitor(mapping, lang).visit(tree).forEach(getParent()::addChild);
  }

  /**
   * Instance AST visitor for provided language
   * @param positions mapping data
   * @param lang the languate
   * @return a visitor
   */
  public ParseTreeVisitor<List<Node>> instanceVisitor(OldMapping positions, EmbeddedLanguage lang) {
    if (EmbeddedLanguage.CICS == lang) {
      return new CICSVisitor(positions);
    }

    if (EmbeddedLanguage.SQL == lang) {
      return new Db2SqlVisitor(positions);
    }
    throw new RuntimeException("Unknown language " + lang);
  }

}
