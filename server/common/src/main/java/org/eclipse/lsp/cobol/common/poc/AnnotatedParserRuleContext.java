package org.eclipse.lsp.cobol.common.poc;

import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;
import java.util.Map;

public class AnnotatedParserRuleContext extends ParserRuleContext {
    @Getter
    Map<String, Object> customData = new HashMap<>();
    public LocalisedDialect dialect;

    public AnnotatedParserRuleContext() {
    }

    public AnnotatedParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

}
