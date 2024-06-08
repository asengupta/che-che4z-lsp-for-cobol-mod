/*
 * Copyright (c) 2022 Broadcom.
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

package org.eclipse.lsp.cobol.implicitDialects.cics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp.cobol.common.error.SyntaxError;
import org.eclipse.lsp.cobol.common.model.tree.Node;

import java.util.*;

/**
 * This visitor analyzes the parser tree for CICS and returns its semantic context as a syntax tree
 */
@Getter
@Slf4j
@AllArgsConstructor
public abstract class ErrorHandlingCICSVisitor extends CICSParserBaseVisitor<List<Node>> {
    protected final List<SyntaxError> errors = new LinkedList<>();
}
