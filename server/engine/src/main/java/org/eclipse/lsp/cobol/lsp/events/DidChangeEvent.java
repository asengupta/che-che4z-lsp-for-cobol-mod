package org.eclipse.lsp.cobol.lsp.events;

import org.eclipse.lsp.cobol.lsp.handlers.text.DidChangeHandler;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;

import java.util.concurrent.ExecutionException;

public class DidChangeEvent implements LspEvent<Void> {
  private final DidChangeHandler handler;
  private final DidChangeTextDocumentParams params;

  public DidChangeEvent(DidChangeHandler handler, DidChangeTextDocumentParams params) {
    this.handler = handler;
    this.params = params;
  }

  @Override
  public Void execute() throws ExecutionException, InterruptedException {
    handler.didChange(params);
    return null;
  }
}
