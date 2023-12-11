package org.eclipse.lsp.cobol.lsp.events;

import java.util.concurrent.ExecutionException;

public class PoisonPillEvent implements LspEvent<Void> {
  public static final PoisonPillEvent instance = new PoisonPillEvent();

  private PoisonPillEvent() {
  }

  @Override
  public Void execute() throws ExecutionException, InterruptedException {
    return null;
  }
}
