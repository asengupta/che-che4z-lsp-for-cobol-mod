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

import org.eclipse.lsp.cobol.lsp.handlers.text.DidCloseHandler;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;

import java.util.concurrent.ExecutionException;

public class DidCloseEvent implements LspEvent<Void> {
  private final DidCloseHandler handler;
  private final DidCloseTextDocumentParams params;

  public DidCloseEvent(DidCloseHandler handler, DidCloseTextDocumentParams params) {
    this.handler = handler;
    this.params = params;
  }

  @Override
  public Void execute() throws ExecutionException, InterruptedException {
    handler.didClose(params);
    return null;
  }
}
