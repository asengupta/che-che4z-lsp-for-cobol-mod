package org.eclipse.lsp.cobol.lsp.events;

import org.eclipse.lsp.cobol.lsp.LspEventCancelCondition;
import org.eclipse.lsp.cobol.lsp.LspEventDependency;
import org.eclipse.lsp.cobol.lsp.handlers.text.CompletionHandler;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CompletionEvent implements LspEvent<Either<List<CompletionItem>, CompletionList>> {
  private final CompletionHandler handler;
  private final CompletionParams params;

  public CompletionEvent(CompletionHandler handler, CompletionParams params) {
    this.handler = handler;
    this.params = params;
  }

  @Override
  public Either<List<CompletionItem>, CompletionList> execute() throws ExecutionException, InterruptedException {
    return handler.completion(params);
  }

  @Override
  public List<LspEventDependency> getDependencies() {
    return LspEvent.super.getDependencies();
  }

  @Override
  public List<LspEventCancelCondition> getCancelConditions() {
    return LspEvent.super.getCancelConditions();
  }
}
