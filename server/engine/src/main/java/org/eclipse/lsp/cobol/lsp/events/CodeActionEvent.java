package org.eclipse.lsp.cobol.lsp.events;

import org.eclipse.lsp.cobol.lsp.handlers.text.CodeActionHandler;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CodeActionEvent implements LspEvent<List<Either<Command, CodeAction>>> {
  private final CodeActionHandler handler;
  private final CodeActionParams params;

  public CodeActionEvent(CodeActionHandler handler, CodeActionParams params) {
    this.handler = handler;
    this.params = params;
  }

  @Override
  public List<Either<Command, CodeAction>> execute() throws ExecutionException, InterruptedException {
    return handler.codeAction(params);
  }
}
