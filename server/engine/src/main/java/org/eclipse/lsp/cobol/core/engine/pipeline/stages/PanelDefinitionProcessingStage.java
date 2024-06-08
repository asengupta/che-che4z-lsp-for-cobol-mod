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
package org.eclipse.lsp.cobol.core.engine.pipeline.stages;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.eclipse.lsp.cobol.common.error.ErrorSeverity;
import org.eclipse.lsp.cobol.common.error.ErrorSource;
import org.eclipse.lsp.cobol.common.error.SyntaxError;
import org.eclipse.lsp.cobol.common.mapping.OriginalLocation;
import org.eclipse.lsp.cobol.common.message.MessageService;
import org.eclipse.lsp.cobol.core.AntlrIdmsPanelDefinitionParser;
import org.eclipse.lsp.cobol.core.PanelDefinitionParser;
import org.eclipse.lsp.cobol.core.engine.analysis.AnalysisContext;
import org.eclipse.lsp.cobol.core.engine.pipeline.Stage;
import org.eclipse.lsp.cobol.core.engine.pipeline.StageResult;
import org.eclipse.lsp.cobol.core.strategy.CobolErrorStrategy;
import org.eclipse.lsp.cobol.core.visitor.ParserListener;
import org.eclipse.lsp4j.Location;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Process compiler options statements in the source file and substitute them with empty lines.
 */
@RequiredArgsConstructor
public class PanelDefinitionProcessingStage implements Stage<IdmsPanelDefinitionParserStageResult, Void> {
    private final MessageService messageService;
    private final ParseTreeListener treeListener;

    @Override
    public StageResult<IdmsPanelDefinitionParserStageResult> run(AnalysisContext ctx, StageResult<Void> prevStageResult) {
        ParserListener listener = new ParserListener(ctx.getExtendedDocument(), null);
        CobolErrorStrategy errorStrategy = new CobolErrorStrategy(messageService);
        AntlrIdmsPanelDefinitionParser parser = new AntlrIdmsPanelDefinitionParser(CharStreams.fromString(ctx.getExtendedDocument().toString()),
                listener, errorStrategy, treeListener);
        PanelDefinitionParser.StartRuleContext tree = parser.runParser();

        ctx.getAccumulatedErrors().addAll(listener.getErrors());
        ctx.getAccumulatedErrors().addAll(getParsingError(ctx, parser));
        return new StageResult<>(new IdmsPanelDefinitionParserStageResult(parser.getTokens(), tree));
    }

    private List<SyntaxError> getParsingError(AnalysisContext context, AntlrIdmsPanelDefinitionParser parser) {
        return parser.diagnostics().stream().map(diagnostic -> {
            Location location = context.getExtendedDocument().mapLocation(diagnostic.getRange());
            return SyntaxError.syntaxError()
                    .errorSource(ErrorSource.PARSING)
                    .severity(ErrorSeverity.ERROR)
                    .location(new OriginalLocation(location, "NULL"))
                    .suggestion(diagnostic.getMessage())
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    public String getName() {
        return "IDMS Panel Definition Parsing";
    }
}
