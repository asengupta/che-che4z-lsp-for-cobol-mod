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

package org.eclipse.lsp.cobol.implicitDialects.cics;

import lombok.Getter;
import org.eclipse.lsp.cobol.common.error.SyntaxError;
import org.eclipse.lsp.cobol.common.model.tree.Node;
import org.eclipse.lsp.cobol.implicitDialects.sql.Db2SqlParserBaseVisitor;

import java.util.LinkedList;
import java.util.List;

/**
 * Marker for flipping between substituting and original
 */
public abstract class MarkerDb2SqlVisitor extends Db2SqlParserBaseVisitor<List<Node>> {
    @Getter
    protected final List<SyntaxError> errors = new LinkedList<>();
}
