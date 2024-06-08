/*
* Copyright (C) 2017, Ulrich Wolffgang <ulrich.wolffgang@proleap.io>
* All rights reserved.
*
* This software may be modified and distributed under the terms
* of the MIT license. See the LICENSE file for details.
*/

parser grammar PanelDefinitionParser;
options {tokenVocab = PanelDefinitionLexer; superClass = MessageServiceParser;}

startRule : compilationUnit EOF;

compilationUnit
   : statement+;

commaSeparator: COMMACHAR | COMMASEPARATOR;

statement : (mapAutoPanelStatement | mfldStatement) DOT_FS;

mapAutoPanelStatement : mapVerb MAP IDENTIFIER versionClause? dateTimeClause? messagePrefixClause?
            autoPanelClause systemClause? residencyClause? usingClause?
            editClause? cursorClause? resetClause? lockKeyboardClause? alarmClause?
            prtClause? carriageReturnClause? pageableClause?
            decimalPointClause? helpClause? onEditErrorClause? originClause?
            ;

mfldStatement : mfldVerb? (MF | MFLD) atClause? occursClause? forClause? atClause? forAttributesClause?
                forDelimitClause? pagingTypeClause? forValueClause? forCursorClause? forSpecClause?;


mfldVerb : ADD | (MOD | MODIFY);
occursClause :OCCURS (ONE | (OCCURRENCE_COUNT INTEGERLITERAL));
forClause : FOR (ALL | deviceCodesList);
atClause : AT (anywhere | positionClause);
positionClause : rowColumnPair+;
forAttributesClause : (ATTR | ATTRIBUTES) EQUALCHAR (NONE | attributesList);
forDelimitClause : delimitNoDelimitVerb delimitSubClause?;
delimitNoDelimitVerb : delimitWord | noDelimitWord;
delimitWord : DELIM | DELIMIT;
noDelimitWord : NODELIM | DELIMIT;
delimitSubClause : (IS | EQUALCHAR)? (SKIP_WORD | NOSKIP);
pagingTypeClause : (PAG | (PAGING TYPE)) (IS | EQUALCHAR)? detailOption;
forValueClause : value (IS | EQUALCHAR)? (dataValue | dataValueOccurrenceClause);
dataValueOccurrenceClause :LPARENCHAR INTEGERLITERAL dataValue RPARENCHAR;
forCursorClause : (CURSOR | NOCURSOR);
forSpecClause : literalWord | messageLength | pageLength | responseLength | dfldSpecsClause;
dfldSpecsClause : DFLD dfldSpecs;

dfldSpecs : dataFieldName subscriptNumber? dfldOfClause? dfldHelpClause? requiredClause? reverseNumericClause?
underscoreWhenBlankClause? automaticEditingClause? editTableClause? codeTableClause? errorMessageClause? messagePrefixClause?
forInputClause? forOutputClause?;

dfldHelpClause : HELP helpSource;
reverseNumericClause : (REV | REVERSE) | (NUM | NUMERIC) (IS | EQUALCHAR) (YES | NO);
underscoreWhenBlankClause : UNDERSCORE WHEN BLANK (YES | NO);
automaticEditingClause : NOEDIT | externalPictureClause;
errorMessageClause : ERROR (MESS | MESSAGE) (messageString | messageID | NULL);
externalPictureClause : (EXT | EXTERNAL) (PIC | PICTURE) (IS | EQUALCHAR) (pictureString | (INT | INTERNAL))
                        zeroRetainClause? displayBlankClause?;
messagePrefixClause : MSG PREFIX (IS | EQUALCHAR) (DC | messagePrefix);
forInputClause : FOR (IN | INPUT)  justifyClause? padClause? dataYesNoClause? uppercaseYesNo? editModuleClause?
                 autoEditClause?;
forOutputClause : FOR OUTPUT backScanClause? outputDataAttribute? editModuleClause? autoEditClause?;

backScanClause : (BACK | BACKSCAN) (YES | NO);
outputDataAttribute : DATA (YES | NO | ERASE | (ATTR | ATTRIBUTE));
justifyClause : (JUST | JUSTIFY) (LEFT | RIGHT);
padClause : PAD (NO | (WITH? padCharacter));
dataYesNoClause : DATA (YES | NO);
uppercaseYesNo : UPPERCASE (YES | NO);
editModuleClause : EDIT (IS | EQUALCHAR) moduleName;
autoEditClause : WITH (AUTO | AUTOEDIT) autoEditOption;
autoEditOption : (NO | (BEF | BEFORE) | (AFT | AFTER));
moduleName : IDENTIFIER;
messagePrefix : IDENTIFIER;
messageString : SINGLE_QUOTE STRINGLITERAL SINGLE_QUOTE;
messageID : IDENTIFIER;
zeroRetainClause : ((ZERO | ZEROED) | (RETAIN | RETAINED)) WHEN NULL;
displayBlankClause : ((DISP | DISPLAY) | BLANK) WHEN ZERO;
editTableClause : EDIT tableClause;
codeTableClause : CODE tableClause;
tableClause : (TAB | TABLE) (IS | EQUALCHAR) (NULL | editTableSubject);
editTableSubject : tableName tableVersion? (LINK | NOLINK) usageType ;
usageType : USAGE IS (validate | invalidate | defaultKeyword);
validate: VALID | VALIDATE;
invalidate : INVALID | INVALIDATE;
defaultKeyword : DEF | DEFAULT;

tableName : STRINGLITERAL;
tableVersion : (VER | VERSION) INTEGERLITERAL;

//padCharacter : SINGLE_CHR_LITERAL | HEX_LITERAL;
padCharacter : NONNUMERICLITERAL;
helpSource : NONE | moduleSource;
moduleSource : (SOU | SOURCE) (NONE | moduleSpec);
moduleSpec : (MOD | MODULE) versionClause? moduleScreenSpec?;
moduleScreenSpec : ((HALF SCREEN) | (FULL SCREEN));
dataFieldName : IDENTIFIER;
recordName : IDENTIFIER;
roleName : IDENTIFIER;
subscriptNumber : INTEGERLITERAL;
dfldOfClause : OF (record | roleName);
record : recordName ((VER | VERSION) INTEGER)?;
requiredClause : ((REQ | REQUIRED) | (OPT | OPTIONAL));
pictureString : SINGLE_QUOTE PICTURE SINGLE_QUOTE;

literalWord : LIT | LITERAL;
messageLength : (MESS | MESSAGE) (LEN | LENGTH) INTEGER;
pageLength : PAGE (LEN | LENGTH) FOUR;
responseLength : (RESP | RESPONSE) (LEN | LENGTH) INTEGER;


dataValue : NONNUMERICLITERAL;
detailOption : detailStart | detailOnly | detailEnd | footerStart | NULL;
detailStart : detail start;
detailOnly : detail ONLY;
detailEnd : detail end;
footerStart : footer start;
value : VAL | VALUE;

detail : DET | DETAIL;
start : ST | START;
end : EN | END;
footer : FOOT | FOOTER;

rowColumnPair : LPARENCHAR numericLiteral commaSeparator numericLiteral RPARENCHAR;
anywhere : ANY | ANYWHERE;
dateTimeClause :DATETIME (IS | EQUALCHAR)? dateTimeStamp;
dateTimeStamp : IDENTIFIER;
autoPanelClause : AUTOPANEL;
systemClause : (SYS | SYSTEM) (IS | EQUALCHAR) IDENTIFIER;
residencyClause : (RES | RESIDENT) | (NONRES | NONRESIDENT);
usingClause : USING recordKeyword? LPARENCHAR records RPARENCHAR;
editClause : (EDIT | NOEDIT);
cursorClause : CURSOR AT IDENTIFIER;
resetClause : (RESET | NORESET) (MODIFIED | MOD);
lockKeyboardClause : (LOCK | UNLOCK) (KEYBOARD | KEY)?;
alarmClause : (ALARM | NOALARM);
prtClause : (STARTPRT | NOPRT);
carriageReturnClause : (NLCR | FORTY_CR | SIXTYFOUR_CR | EIGHTY_CR);
pageableClause : (pageable | nonPageable);
decimalPointClause : decimalPoint (IS | EQUALCHAR)? (COMMA | PERIOD_WORD);
helpClause : HELP;
onEditErrorClause : ON EDIT ERROR incorrectFieldsAttributesClause? correctFieldsAttributesClause? soundClause?;
incorrectFieldsAttributesClause : INCORRECT FIELDS (ATTR | ATTRIBUTES) EQUALCHAR? attributesList;
correctFieldsAttributesClause : CORRECT FIELDS (ATTR | ATTRIBUTES) EQUALCHAR? attributesList;
soundClause : SOUND ALARM | NOALARM;
originClause : ORIGIN FOR (ALL | records) (IS | EQUALCHAR) LPARENCHAR row column RPARENCHAR;

deviceCodesList : LPARENCHAR deviceCodes RPARENCHAR;
deviceCodes : deviceCode* deviceCode;
deviceCode : IDENTIFIER;

row : INTEGERLITERAL;
column : INTEGERLITERAL;

attributesList : LPARENCHAR possibleAttribute (commaSeparator possibleAttribute)* RPARENCHAR;

possibleAttribute : (attributeAlphanumericOption | attributeProtectedOption | skipOption | attributeDetectableOption |
                                        attributeDisplayOption | attributeMdtOption | attributeBlinkOption | attributeVideoOption |
                                        attributeUnderscoreOption | attributeAllLineOption | attributeLeftLineOption | attributeRightLineOption |
                                        attributeBottomLineOption | attributeTopLineOption | attributeColorOption);

attributeAlphanumericOption : (ALPHANUMERIC | ALPHA) | (NUMERIC | NUM);
attributeProtectedOption : (PROT | PROTECTED) |(UNPROT | UNPROTECTED);
skipOption : SKIP_WORD;
attributeDetectableOption : (DETECT | DETECTABLE) | (NONDETECT | NONDETECTABLE);
attributeDisplayOption : (DISP | DISPLAY) | DARK | BRIGHT;
attributeMdtOption : MDT | NOMDT;
attributeBlinkOption : BLINK | NOBLINK;
attributeVideoOption : (REV | REVERSE_VIDEO) | (NORM | NORMAL_VIDEO);
attributeUnderscoreOption : (UNDER | UNDERSCORE) | (NOUNDER | NOUNDERSCORE);
attributeAllLineOption : (ALLL | ALLLINE) | (NOLI | NOLINE);
attributeLeftLineOption : (LEFT | LEFTLINE) | (NOLEFT | NOLEFTLINE);
attributeRightLineOption : (RIGHT | RIGHTLINE) | (NORIGHT | NORIGHTLINE);
attributeBottomLineOption : (BOTTOM | BOTTOMLINE) | (NOBOTTOM | NOBOTTOMLINE);
attributeTopLineOption : (TOP | TOPLINE) | (NOTOP | NOTOPLINE);
attributeColorOption : blueOption | redOption | pinkOption | greenOption | turquoiseOption | yellowOption |
                       whiteOption | noColorOption;

blueOption : BL | BLUE;
redOption : RED;
pinkOption : PIN | PINK;
greenOption : GRE | GREEN;
turquoiseOption : TUR | TURQUOISE;
yellowOption : YEL | YELLOW;
whiteOption : WHI | WHITE;
noColorOption : NOC | NOCOLOR;

decimalPoint : (DEC | DECIMAL) POINT;
pageable : (PAGE | PAGEABLE);
nonPageable : (NON_PAG | NON_PAGEABLE);

recordKeyword : RECORDS | REC;
records : recordIdentifier recordIdentifier*;
recordIdentifier : IDENTIFIER version? roleClause?;
roleClause : (ROLE | ROLENAME) IDENTIFIER;
mapVerb : (ADD | MODIFY DELETE);
version : (VERSION | VER);
versionClause : version (IS | EQUALCHAR)? IDENTIFIER;


// identifier ----------------------------------

generalIdentifier
   : IDENTIFIER
   ;

// names ----------------------------------

numericLiteral
   : NUMERICLITERAL | ZERO | integerLiteral
   ;

integerLiteral
   : INTEGERLITERAL | LEVEL_NUMBER | LEVEL_NUMBER_66 | LEVEL_NUMBER_77 | LEVEL_NUMBER_88
   ;

literal
   : NONNUMERICLITERAL | numericLiteral | utfLiteral | hexadecimalUtfLiteral
   ;

utfLiteral: U_CHAR NONNUMERICLITERAL;

hexadecimalUtfLiteral: U_CHAR HEX_NUMBERS;
