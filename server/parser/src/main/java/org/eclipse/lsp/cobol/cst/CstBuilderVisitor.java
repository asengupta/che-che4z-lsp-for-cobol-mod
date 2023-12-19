package org.eclipse.lsp.cobol.cst;

import org.eclipse.lsp.cobol.core.CobolParser;
import org.eclipse.lsp.cobol.core.CobolParserBaseVisitor;
import org.eclipse.lsp.cobol.cst.nodes.CompilationUnit;

public class CstBuilderVisitor extends CobolParserBaseVisitor<CompilationUnit> {
  @Override
  public CompilationUnit visitCompilationUnit(CobolParser.CompilationUnitContext ctx) {
    return new CompilationUnit();
  }
}
