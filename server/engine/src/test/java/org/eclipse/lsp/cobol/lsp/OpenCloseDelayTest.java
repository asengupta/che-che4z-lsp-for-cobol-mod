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
package org.eclipse.lsp.cobol.lsp;

import org.eclipse.lsp.cobol.lsp.events.CodeActionEvent;
import org.eclipse.lsp.cobol.lsp.events.DidCloseEvent;
import org.eclipse.lsp.cobol.lsp.events.DidOpenEvent;
import org.eclipse.lsp.cobol.lsp.events.LspEvent;
import org.eclipse.lsp.cobol.lsp.handlers.text.CodeActionHandler;
import org.eclipse.lsp.cobol.lsp.handlers.text.DidCloseHandler;
import org.eclipse.lsp.cobol.lsp.handlers.text.DidOpenHandler;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;

/**
 * Tests event delays in didOpen/didClose sequences.
 */
class OpenCloseDelayTest {
  @Test
  void openCloseTest() throws InterruptedException {
    DidCloseHandler didCloseHandler = mock(DidCloseHandler.class);
    DidCloseTextDocumentParams didCloseParams = mock(DidCloseTextDocumentParams.class);

    LspEvent<?> didClose = new DidCloseEvent(didCloseHandler, didCloseParams);
    DidOpenHandler didOpenHandler = mock(DidOpenHandler.class);
    DidOpenTextDocumentParams didOpenParams = mock(DidOpenTextDocumentParams.class);
    DidOpenEvent didOpen = new DidOpenEvent(didOpenHandler, didOpenParams);
    LspMessageDispatcher dispatcher = new LspMessageDispatcher();
    CompletableFuture<Void> loop = dispatcher.startEventLoop();

    dispatcher.publish(didOpen);
    dispatcher.publish();
    dispatcher.publish(didClose);

    dispatcher.stop();
    loop.join();
  }
}
