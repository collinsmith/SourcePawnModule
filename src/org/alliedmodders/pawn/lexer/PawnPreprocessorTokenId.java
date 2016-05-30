package org.alliedmodders.pawn.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public enum PawnPreprocessorTokenId implements TokenId {

    LEXICAL_ERROR(null, "preprocessor-error"),
    
    IDENTIFIER(null, "preprocessor-identifier"),
    TAG(null, "tag"),
    
    ASSERT("assert", "preprocessor-directive"),
    DEFINE("define", "preprocessor-directive"),
    ELSE("else", "preprocessor-directive"),
    ELSEIF("elseif", "preprocessor-directive"),
    ENDIF("endif", "preprocessor-directive"),
    ENDINPUT("endinput", "preprocessor-directive"),
    ERROR("error", "preprocessor-directive"),
    FILE("file", "preprocessor-directive"),
    IF("if", "preprocessor-directive"),
    INCLUDE("include", "preprocessor-directive"),
    LINE("line", "preprocessor-directive"),
    PRAGMA("pragma", "preprocessor-directive"),
    SECTION("section", "preprocessor-directive"),
    TRYINCLUDE("tryinclude", "preprocessor-directive"),
    UNDEF("undef", "preprocessor-directive"),
    
    KEYWORD_ASSERT("assert", "keyword"),
    KEYWORD_BREAK("break", "keyword-directive"),
    KEYWORD_CASE("case", "keyword-directive"),
    KEYWORD_CONST("const", "keyword"),
    KEYWORD_CONTINUE("continue", "keyword-directive"),
    KEYWORD_DEFAULT("default", "keyword-directive"),
    KEYWORD_DO("do", "keyword-directive"),
    KEYWORD_ELSE("else", "keyword-directive"),
    KEYWORD_ENUM("enum", "keyword"),
    KEYWORD_FALSE("false", "literal"),
    KEYWORD_FOR("for", "keyword-directive"),
    KEYWORD_FORWARD("forward", "keyword"),
    KEYWORD_GOTO("goto", "keyword-directive"),
    KEYWORD_IF("if", "keyword-directive"),
    KEYWORD_NATIVE("native", "keyword"),
    KEYWORD_NEW("new", "keyword"),
    KEYWORD_OPERATOR("operator", "keyword"),
    KEYWORD_PUBLIC("public", "keyword"),
    KEYWORD_RETURN("return", "keyword-directive"),
    KEYWORD_SIZEOF("sizeof", "keyword-directive"),
    KEYWORD_STATIC("static", "keyword"),
    KEYWORD_STOCK("stock", "keyword"),
    KEYWORD_SWITCH("switch", "keyword-directive"),
    KEYWORD_TAGOF("tagof", "keyword-directive"),
    KEYWORD_TRUE("true", "literal"),
    KEYWORD_WHILE("while", "keyword-directive"),
        
    INCLUDE_SYSTEM_FILE(null, "preprocessor-include"),
    INCLUDE_RELATIVE_FILE(null, "preprocessor-include"),
    
    CELL_LITERAL(null, "number"),
    FLOAT_LITERAL(null, "number"),
    CHAR_LITERAL(null, "character"),
    STRING_LITERAL(null, "string"),
    
    PRAGMA_ALIGN("align", "preprocessor-pragma"),
    PRAGMA_AMXLIMIT("amxlimit", "preprocessor-pragma"),
    PRAGMA_AMXRAM("amxram", "preprocessor-pragma"),
    PRAGMA_CODEPAGE("codepage", "preprocessor-pragma"),
    PRAGMA_CTRLCHAR("ctrlchar", "preprocessor-pragma"),
    PRAGMA_DEPRECATED("deprecated", "preprocessor-pragma"),
    PRAGMA_DYNAMIC("dynamic", "preprocessor-pragma"),
    PRAGMA_LIBRARY("library", "preprocessor-pragma"),
    PRAGMA_LOADLIB("loadlib", "preprocessor-pragma"),
    PRAGMA_NEWDECLS("newdecls", "preprocessor-pragma"),
    PRAGMA_OVERLAY("overlay", "preprocessor-pragma"),
    PRAGMA_RATIONAL("rational", "preprocessor-pragma"),
    PRAGMA_REQLIB("reqlib", "preprocessor-pragma"),
    PRAGMA_SEMICOLON("semicolon", "preprocessor-pragma"),
    PRAGMA_TABSIZE("tabsize", "preprocessor-pragma"),
    
    IF_DEFINED("defined", "preprocessor-if"),
    
    MACRO_PARAM(null, "preprocessor-macro-param"),
    
    EQ("=", "operator"),
    GT(">", "operator"),
    LT("<", "operator"),
    BANG("!", "operator"),
    TILDE("~", "operator"),
    QUESTION("?", "operator"),
    COLON(":", "operator"),
    EQEQ("==", "operator"),
    LTEQ("<=", "operator"),
    GTEQ(">=", "operator"),
    BANGEQ("!=","operator"),
    AMPAMP("&&", "operator"),
    BARBAR("||", "operator"),
    PLUSPLUS("++", "operator"),
    MINUSMINUS("--","operator"),
    PLUS("+", "operator"),
    MINUS("-", "operator"),
    STAR("*", "operator"),
    SLASH("/", "operator"),
    AMP("&", "operator"),
    BAR("|", "operator"),
    CARET("^", "operator"),
    PERCENT("%", "operator"),
    LTLT("<<", "operator"),
    GTGT(">>", "operator"),
    GTGTGT(">>>", "operator"),
    PLUSEQ("+=", "operator"),
    MINUSEQ("-=", "operator"),
    STAREQ("*=", "operator"),
    SLASHEQ("/=", "operator"),
    AMPEQ("&=", "operator"),
    BAREQ("|=", "operator"),
    CARETEQ("^=", "operator"),
    PERCENTEQ("%=", "operator"),
    LTLTEQ("<<=", "operator"),
    GTGTEQ(">>=", "operator"),
    GTGTGTEQ(">>>=", "operator"),
    
    ELLIPSIS("...", "special"),
    UNDERSCORE("_", "special"),
    
    LPAREN("(", "separator"),
    RPAREN(")", "separator"),
    LBRACE("{", "separator"),
    RBRACE("}", "separator"),
    LBRACKET("[", "separator"),
    RBRACKET("]", "separator"),
    SEMICOLON(";", "separator"),
    COMMA(",", "separator"),
    DOT(".", "separator"),
    
    EOL(null, "whitespace"),
    WHITESPACE(null, "whitespace"),
    
    LINE_CONCATENATION(null, "line-concatenation"),
    
    OTHER_TEXT(null, "preprocessor"),
    FLOAT_LITERAL_INVALID(null, "number")
    ;

    private final String fixedText;
    private final String primaryCategory;
    
    private PawnPreprocessorTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }
    
    @Override
    public String primaryCategory() {
        return primaryCategory;
    }
    
    public static Language<PawnPreprocessorTokenId> language() {
        return language;
    }
    
    private static final Language<PawnPreprocessorTokenId> language = new LanguageHierarchy<PawnPreprocessorTokenId>() {

        @Override
        protected String mimeType() {
            return "text/x-pawn-preprocessor";
        }

        @Override
        protected Collection<PawnPreprocessorTokenId> createTokenIds() {
            return EnumSet.allOf(PawnPreprocessorTokenId.class);
        }

        @Override
        protected Map<String, Collection<PawnPreprocessorTokenId>> createTokenCategories() {
            Map<String, Collection<PawnPreprocessorTokenId>> categories = new HashMap<>();
            
            EnumSet<PawnPreprocessorTokenId> literals = EnumSet.of(
                PawnPreprocessorTokenId.CELL_LITERAL,
                PawnPreprocessorTokenId.FLOAT_LITERAL,
                PawnPreprocessorTokenId.CHAR_LITERAL,
                PawnPreprocessorTokenId.STRING_LITERAL
            );
            
            categories.put("literal", literals);
            
            EnumSet<PawnPreprocessorTokenId> pragmas = EnumSet.of(
                PawnPreprocessorTokenId.PRAGMA_ALIGN,
                PawnPreprocessorTokenId.PRAGMA_AMXLIMIT,
                PawnPreprocessorTokenId.PRAGMA_AMXRAM,
                PawnPreprocessorTokenId.PRAGMA_CODEPAGE,
                PawnPreprocessorTokenId.PRAGMA_CTRLCHAR,
                PawnPreprocessorTokenId.PRAGMA_DEPRECATED,
                PawnPreprocessorTokenId.PRAGMA_DYNAMIC,
                PawnPreprocessorTokenId.PRAGMA_LIBRARY,
                PawnPreprocessorTokenId.PRAGMA_LOADLIB,
                PawnPreprocessorTokenId.PRAGMA_OVERLAY,
                PawnPreprocessorTokenId.PRAGMA_RATIONAL,
                PawnPreprocessorTokenId.PRAGMA_REQLIB,
                PawnPreprocessorTokenId.PRAGMA_SEMICOLON,
                PawnPreprocessorTokenId.PRAGMA_TABSIZE
            );
            
            categories.put("pragma", pragmas);
            
            EnumSet<PawnPreprocessorTokenId> keywords = EnumSet.of(
                PawnPreprocessorTokenId.KEYWORD_ASSERT,
                PawnPreprocessorTokenId.KEYWORD_BREAK,
                PawnPreprocessorTokenId.KEYWORD_CASE,
                PawnPreprocessorTokenId.KEYWORD_CONST,
                PawnPreprocessorTokenId.KEYWORD_CONTINUE,
                PawnPreprocessorTokenId.KEYWORD_DEFAULT,
                PawnPreprocessorTokenId.KEYWORD_DO,
                PawnPreprocessorTokenId.KEYWORD_ELSE,
                PawnPreprocessorTokenId.KEYWORD_ENUM,
                PawnPreprocessorTokenId.KEYWORD_FALSE,
                PawnPreprocessorTokenId.KEYWORD_FOR,
                PawnPreprocessorTokenId.KEYWORD_FORWARD,
                PawnPreprocessorTokenId.KEYWORD_GOTO,
                PawnPreprocessorTokenId.KEYWORD_IF,
                PawnPreprocessorTokenId.KEYWORD_NATIVE,
                PawnPreprocessorTokenId.KEYWORD_NEW,
                PawnPreprocessorTokenId.KEYWORD_OPERATOR,
                PawnPreprocessorTokenId.KEYWORD_PUBLIC,
                PawnPreprocessorTokenId.KEYWORD_RETURN,
                PawnPreprocessorTokenId.KEYWORD_SIZEOF,
                PawnPreprocessorTokenId.KEYWORD_STATIC,
                PawnPreprocessorTokenId.KEYWORD_STOCK,
                PawnPreprocessorTokenId.KEYWORD_SWITCH,
                PawnPreprocessorTokenId.KEYWORD_TAGOF,
                PawnPreprocessorTokenId.KEYWORD_TRUE,
                PawnPreprocessorTokenId.KEYWORD_WHILE
            );
            
            categories.put("keyword", keywords);
            
            EnumSet<PawnPreprocessorTokenId> ifs = EnumSet.of(
                PawnPreprocessorTokenId.IF_DEFINED
            );
            
            categories.put("if", ifs);
            
            return categories;
        }

        @Override
        protected Lexer<PawnPreprocessorTokenId> createLexer(LexerRestartInfo<PawnPreprocessorTokenId> info) {
            return new PawnPreprocessorLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<PawnPreprocessorTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            switch (token.id()) {
                case STRING_LITERAL:
                    return LanguageEmbedding.create(PawnStringLiteralTokenId.language(), 1,
                            (token.partType() == PartType.COMPLETE) ? 1 : 0);
                case CHAR_LITERAL:
                    return LanguageEmbedding.create(PawnCharacterLiteralTokenId.language(), 1,
                            (token.partType() == PartType.COMPLETE) ? 1 : 0);
            }
            
            return null; // no embedding
        }
        
    }.language();
    
}
