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
package org.eclipse.lsp.cobol.lsp.events;

import org.eclipse.lsp.cobol.lsp.handlers.text.DidOpenHandler;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;

import java.util.concurrent.ExecutionException;

public class DidOpenEvent implements LspEvent<Void> {
  private final DidOpenHandler handler;
  private final DidOpenTextDocumentParams params;

  public DidOpenEvent(DidOpenHandler handler, DidOpenTextDocumentParams params) {
    this.handler = handler;
    this.params = params;
  }

  @Override
  public Void execute() throws ExecutionException, InterruptedException {
    handler.didOpen(params);
    return null;
  }
}
