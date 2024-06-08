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

import lombok.Getter;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.lsp.cobol.core.PanelDefinitionParser;

/**
 * Result of IDMS Panel Definition Parsing Stage
 */
public class IdmsPanelDefinitionParserStageResult {
    @Getter private CommonTokenStream tokens;
    @Getter private PanelDefinitionParser.StartRuleContext tree;

    public IdmsPanelDefinitionParserStageResult(CommonTokenStream tokens, PanelDefinitionParser.StartRuleContext tree) {
        this.tokens = tokens;
        this.tree = tree;
    }
}
