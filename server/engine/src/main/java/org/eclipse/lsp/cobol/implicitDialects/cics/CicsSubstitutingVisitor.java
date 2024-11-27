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

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lsp.cobol.common.dialects.CobolDialect;
import org.eclipse.lsp.cobol.common.dialects.DialectProcessingContext;
import org.eclipse.lsp.cobol.common.error.ErrorSeverity;
import org.eclipse.lsp.cobol.common.error.ErrorSource;
import org.eclipse.lsp.cobol.common.error.SyntaxError;
import org.eclipse.lsp.cobol.common.message.MessageService;
import org.eclipse.lsp.cobol.common.model.Locality;
import org.eclipse.lsp.cobol.common.model.tree.CodeBlockUsageNode;
import org.eclipse.lsp.cobol.common.model.tree.CompilerDirectiveNode;
import org.eclipse.lsp.cobol.common.model.tree.Node;
import org.eclipse.lsp.cobol.common.model.tree.StopNode;
import org.eclipse.lsp.cobol.common.model.tree.variable.QualifiedReferenceNode;
import org.eclipse.lsp.cobol.common.model.tree.variable.VariableUsageNode;
import org.eclipse.lsp.cobol.common.poc.AnnotatedParserRuleContext;
import org.eclipse.lsp.cobol.common.poc.PersistentData;
import org.eclipse.lsp.cobol.implicitDialects.cics.nodes.ExecCicsHandleNode;
import org.eclipse.lsp.cobol.implicitDialects.cics.nodes.ExecCicsNode;
import org.eclipse.lsp.cobol.implicitDialects.cics.nodes.ExecCicsReturnNode;
import org.eclipse.lsp.cobol.implicitDialects.cics.utility.VisitorUtility;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.antlr.v4.runtime.Lexer.HIDDEN;

/**
 * This visitor analyzes the parser tree for CICS and returns its semantic context as a syntax tree
 */
@Slf4j
class CicsSubstitutingVisitor extends ErrorHandlingCICSVisitor {
  private static final java.util.logging.Logger LOGGER = Logger.getLogger(CicsSubstitutingVisitor.class.getName());
  private final DialectProcessingContext context;
  private final MessageService messageService;

//  @Getter private final List<SyntaxError> errors = new LinkedList<>();
  @Getter private int extractions = 0;

    CicsSubstitutingVisitor(DialectProcessingContext context, MessageService messageService) {
        this.context = context;
        this.messageService = messageService;
    }

    @Override
  public List<Node> visitQualifiedDataName(CICSParser.QualifiedDataNameContext ctx) {
    return addTreeNode(ctx, QualifiedReferenceNode::new);
  }

  @Override
  public List<Node> visitCicsExecBlock(CICSParser.CicsExecBlockContext ctx) {
    areaBWarning(ctx);
    replaceWithMetadata(ctx, "");
//    addReplacementContext(ctx);

    boolean isReturn = (ctx.allCicsRule() != null && ctx.allCicsRule().size() > 0 && ctx.allCicsRule().get(0).cics_return() != null);
    boolean isHandle = (ctx.allCicsRule() != null && ctx.allCicsRule().size() > 0 && ctx.allCicsRule().get(0).cics_handle() != null);

      if (isReturn) {
          return addTreeNode(ctx, ExecCicsReturnNode::new);
      } else if (isHandle) {
          boolean isProgram = Optional.ofNullable(ctx.allCicsRule().get(0).cics_handle())
                  .map(CICSParser.Cics_handleContext::cics_handle_abend)
                  .map(CICSParser.Cics_handle_abendContext::PROGRAM)
                  .filter(s -> s.size() > 0)
                  .isPresent();

          boolean isLabel = Optional.ofNullable(ctx.allCicsRule().get(0).cics_handle())
                  .map(CICSParser.Cics_handleContext::cics_handle_abend)
                  .map(CICSParser.Cics_handle_abendContext::LABEL)
                  .filter(s -> s.size() > 0)
                  .isPresent();

          boolean isReset = Optional.ofNullable(ctx.allCicsRule().get(0).cics_handle())
                  .map(CICSParser.Cics_handleContext::cics_handle_abend)
                  .map(CICSParser.Cics_handle_abendContext::RESET)
                  .filter(s -> s.size() > 0)
                  .isPresent();

          ExecCicsHandleNode.HandleAbendType type;
          if (isProgram) {
              type = ExecCicsHandleNode.HandleAbendType.PROGRAM;
          } else if (isLabel) {
              type = ExecCicsHandleNode.HandleAbendType.LABEL;
          } else if (isReset) {
              type = ExecCicsHandleNode.HandleAbendType.RESET;
          } else {
              type = ExecCicsHandleNode.HandleAbendType.CANCEL;
          }

          return addTreeNode(ctx, (location) -> new ExecCicsHandleNode(location, type));
      }
      return addTreeNode(ctx, ExecCicsNode::new);
  }

  @Override
  public List<Node> visitCicsDfhRespLiteral(CICSParser.CicsDfhRespLiteralContext ctx) {
    replaceWithMetadata(ctx, "");
//    addReplacementContext(ctx);
    return visitChildren(ctx);
  }

  @Override
  public List<Node> visitCompilerXOpts(CICSParser.CompilerXOptsContext ctx) {
    addReplacementContext(ctx);
    return visitChildren(ctx);
  }

  @Override
  public List<Node> visitAllExciRules(CICSParser.AllExciRulesContext ctx) {
    // TODO: uncomment and adjust below when we decide to support this feature based on compiler directive
    //    boolean isExciModeEnabled = context
    //            .getConfig()
    //            .getCompilerOptions()
    //            .stream()
    //            .anyMatch(str -> str.equalsIgnoreCase("EXCI"));
    //    if (!isExciModeEnabled) {
    //      Locality tokenLocality = getTokenLocality(ctx.start);
    //      errors.add(SyntaxError.syntaxError()
    //              .errorSource(ErrorSource.PARSING)
    //              .location(tokenLocality.toOriginalLocation())
    //              .suggestion(messageService.getMessage("cics.exci.errormessage"))
    //              .severity(ErrorSeverity.WARNING)
    //              .build());
    //    }
    return visitChildren(ctx);
  }

  @Override
  public List<Node> visitCompilerXOptsOption(CICSParser.CompilerXOptsOptionContext ctx) {
    if (Objects.nonNull(ctx.EXCI())) {
      return addTreeNode(
          ctx,
          locality ->
              new CompilerDirectiveNode(locality, ctx.EXCI().getText(), CICSDialect.DIALECT_NAME));
    }
    return visitChildren(ctx);
  }

  @Override
  public List<Node> visitCicsDfhValueLiteral(CICSParser.CicsDfhValueLiteralContext ctx) {
//      replaceWithMetadata(ctx, "");
    addReplacementContext(ctx);
    return visitChildren(ctx);
  }

  @Override
  public List<Node> visitDataName(CICSParser.DataNameContext ctx) {
    return addTreeNode(ctx, locality -> new VariableUsageNode(getName(ctx), locality));
  }

  // TODO : correct the implementation
  @Override
  public List<Node> visitParagraphNameUsage(CICSParser.ParagraphNameUsageContext ctx) {
    String name = getName(ctx);
    Locality locality = buildNameRangeLocality(ctx, name, context.getProgramDocumentUri());
    //    locality.setRange(RangeUtils.shiftRangeWithPosition(position, locality.getRange()));

    Location location = context.getExtendedDocument().mapLocation(locality.getRange());

    Node node =
        new CodeBlockUsageNode(
            Locality.builder().range(location.getRange()).uri(location.getUri()).build(), name);
    visitChildren(ctx).forEach(node::addChild);
    return ImmutableList.of(node);
  }

  @Override
  public List<Node> visitChildren(RuleNode node) {
    return super.visitChildren(node);
  }

  @Override
  protected List<Node> defaultResult() {
    return ImmutableList.of();
  }

  @Override
  protected List<Node> aggregateResult(List<Node> aggregate, List<Node> nextResult) {
    return Stream.concat(aggregate.stream(), nextResult.stream()).collect(toList());
  }

  @Override
  public List<Node> visitCics_return(CICSParser.Cics_returnContext ctx) {
    return addTreeNode(ctx, StopNode::new);
  }

  private List<Node> addTreeNode(ParserRuleContext ctx, Function<Locality, Node> nodeConstructor) {
    Node node = nodeConstructor.apply(VisitorUtility.constructLocality(ctx, context));
    visitChildren(ctx).forEach(node::addChild);
    return ImmutableList.of(node);
  }

  private String getName(ParserRuleContext context) {
    return ofNullable(context).map(RuleContext::getText).map(String::toUpperCase).orElse("");
  }

  /**
   * Builds context name locality based on the name and uri of the document
   *
   * @param ctx is a parse rule context
   * @param name is a name of the entity
   * @param uri is an uri of the document
   * @return locality object
   */
  public Locality buildNameRangeLocality(ParserRuleContext ctx, String name, String uri) {
    Range range =
        new Range(
            new Position(ctx.start.getLine() - 1, ctx.start.getCharPositionInLine()),
            new Position(
                ctx.stop.getLine() - 1, ctx.start.getCharPositionInLine() + name.length()));

    return Locality.builder().uri(uri).range(range).build();
  }

    private void replaceWithMetadata(AnnotatedParserRuleContext ctx, String staticPrefix) {
        String contextTextReference = PersistentData.next();
        ctx.getCustomData().put("IDMS-" + contextTextReference, new Object());
        ctx.getCustomData().put("DIALECT", "CICS");
        String terminator = ".".equals(ctx.stop.getText()) ? "." : "";
        String prefix = String.format("%s_DIALECT_ %s %s", staticPrefix, contextTextReference, terminator);
//        addReplacementContext(ctx, prefix);
      String replacedDialectText = context
              .getExtendedDocument()
              .toString()
              .substring(ctx.start.getStartIndex(), ctx.stop.getStopIndex() + 1);
      LOGGER.info(String.format("Replaced dialect text number [%s] : %s\n", contextTextReference, replacedDialectText));
      String newText =
                prefix
                        + replacedDialectText
                        .replaceAll("[^ \n]", CobolDialect.FILLER);
        context.getExtendedDocument().replace(constructRange(ctx), newText);
        extractions++;
    }

    public Range constructRange(ParserRuleContext ctx) {
        return new Range(
                new Position(
                        ctx.getStart().getLine() - 1,
                        ctx.getStart().getCharPositionInLine()),
                new Position(
                        ctx.getStop().getLine() - 1,
                        ctx.getStop().getCharPositionInLine() + ctx.getStop().getStopIndex() - ctx.getStop().getStartIndex() + 1)
        );
    }
    private void addReplacementContext(AnnotatedParserRuleContext ctx) {
      addReplacementContext(ctx, "");
    }

    private void addReplacementContext(AnnotatedParserRuleContext ctx, String prefix) {
    getAllTerminalNodes(ctx)
        .forEach(
            node -> {
//                String contextTextReference = PersistentData.next();
//                ctx.getCustomData().put("IDMS-" + contextTextReference, new Object());
                context
                        .getExtendedDocument()
                        .replace(
                                constructRange(node),
                                prefix
                                        + StringUtils.repeat(CobolDialect.FILLER, node.getText().length()));
            });
  }

  private List<TerminalNode> getAllTerminalNodes(ParserRuleContext ctx) {
    List<TerminalNode> result = new ArrayList<>();
    for (int childNodes = 0; childNodes < ctx.getChildCount(); childNodes++) {
      ParseTree child = ctx.getChild(childNodes);
      if (child instanceof TerminalNode) {
        result.add((TerminalNode) child);
      } else {
        result.addAll(getAllTerminalNodes((ParserRuleContext) child));
      }
    }
    return result;
  }

  public Range constructRange(TerminalNode ctx) {
    return new Range(
        new Position(ctx.getSymbol().getLine() - 1, ctx.getSymbol().getCharPositionInLine()),
        new Position(
            ctx.getSymbol().getLine() - 1,
            ctx.getSymbol().getCharPositionInLine()
                + ctx.getSymbol().getStopIndex()
                - ctx.getSymbol().getStartIndex()));
  }

  private void areaBWarning(ParserRuleContext ctx) {
    List<Token> tokenList =
        getAllTerminalNodes(ctx).stream().map(TerminalNode::getSymbol).collect(toList());
    areaBWarning(tokenList);
  }

  private void areaBWarning(@NonNull List<Token> tokenList) {
    tokenList.forEach(
        token ->
            Optional.ofNullable(getTokenLocality(token))
                .filter(startsInAreaA(token))
                .ifPresent(
                    locality ->
                        throwException(
                            token.getText(),
                            locality,
                            messageService.getMessage("CobolVisitor.AreaBWarningMsg"))));
  }

  private Locality getTokenLocality(Token token) {
    return Locality.builder()
        .uri(context.getProgramDocumentUri())
        .range(buildTokenRange(token))
        .build();
  }

  private Predicate<Locality> startsInAreaA(Token token) {
    return it -> {
      int charPosition = it.getRange().getStart().getCharacter();
      return charPosition > 6 && charPosition < 11 && token.getChannel() != HIDDEN;
    };
  }

  private void throwException(String wrongToken, @NonNull Locality locality, String message) {
    SyntaxError error =
        SyntaxError.syntaxError()
            .errorSource(ErrorSource.PARSING)
            .location(locality.toOriginalLocation())
            .suggestion(message + wrongToken)
            .severity(ErrorSeverity.WARNING)
            .build();

    LOG.debug("Syntax error by CobolVisitor#throwException: {}", error);
    if (!errors.contains(error)) {
      errors.add(error);
    }
  }

  /**
   * Builds context name locality based on the name and uri of the document
   *
   * @param token is a token
   * @return range object
   */
  public Range buildTokenRange(Token token) {
    return new Range(
        new Position(token.getLine() - 1, token.getCharPositionInLine()),
        new Position(
            token.getLine() - 1, token.getCharPositionInLine() + token.getText().length()));
  }
}
