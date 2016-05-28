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

public enum PawnTokenId implements TokenId {

    PREPROCESSOR_DIRECTIVE(null, "preprocessor-directive"), // Token includes ending new-line
    //EOL(null, "eol"),
    
    IDENTIFIER(null, "identifier"),
    TAG(null, "tag"),
    
    ASSERT("assert", "keyword"),
    BREAK("break", "keyword-directive"),
    CASE("case", "keyword-directive"),
    CONST("const", "keyword"),
    CONTINUE("continue", "keyword-directive"),
    DECL("default", "keyword"),
    DEFAULT("default", "keyword-directive"),
    DELETE("delete", "keyword-directive"),
    DO("do", "keyword-directive"),
    ELSE("else", "keyword-directive"),
    ENUM("enum", "keyword"),
    FOR("for", "keyword-directive"),
    FORWARD("forward", "keyword"),
    GOTO("goto", "keyword-directive"),
    IF("if", "keyword-directive"),
    IN("in", "keyword-directive"),
    METHODMAP("methodmap", "keyword"),
    NATIVE("native", "keyword"),
    NEW("new", "keyword"),
    NULL("null", "keyword"),
    OPERATOR("operator", "keyword"),
    PUBLIC("public", "keyword"),
    RETURN("return", "keyword-directive"),
    SIZEOF("sizeof", "keyword-directive"),
    STATE("state", "keyword-directive"),
    STATIC("static", "keyword"),
    STOCK("stock", "keyword"),
    SWITCH("switch", "keyword-directive"),
    TAGOF("tagof", "keyword-directive"),
    THIS("this", "keyword"),
    TYPEDEF("typedef", "keyword"),
    VIEW_AS("view_as", "keyword-directive"),
    WHILE("while", "keyword-directive"),

    BOOL("bool", "tag"),
    CHAR("char", "tag"),
    FLOAT("float", "tag"),
    INT("int", "tag"),
    VOID("void", "tag"),
    
    _TAG("_:", "tag"),
    ANYTAG("any:", "tag"),
    BOOLTAG("bool:", "tag"),
    FLOATTAG("Float:", "tag"),
    STRINGTAG("String:", "tag"),
    
    CELL_LITERAL(null, "number"),
    FLOAT_LITERAL(null, "number"),
    CHAR_LITERAL(null, "character"),
    STRING_LITERAL(null, "string"),
    
    TRUE("true", "literal"),
    FALSE("false", "literal"),
    
    LPAREN("(", "separator"),
    RPAREN(")", "separator"),
    LBRACE("{", "separator"),
    RBRACE("}", "separator"),
    LBRACKET("[", "separator"),
    RBRACKET("]", "separator"),
    SEMICOLON(";", "separator"),
    COMMA(",", "separator"),
    DOT(".", "separator"),
    
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
    
    WHITESPACE(null, "whitespace"),
    LINE_COMMENT(null, "comment"), // Token includes ending new-line
    BLOCK_COMMENT(null, "comment"),
    DOC_COMMENT(null, "comment"),
    
    // Errors
    INVALID_COMMENT_END("*/", "error"),
    ERROR(null, "error"),
    FLOAT_LITERAL_INVALID(null, "number")
    ;

    private final String fixedText;
    private final String primaryCategory;
    
    private PawnTokenId(String fixedText, String primaryString) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryString;
    }
    
    public String fixedText() {
        return fixedText;
    }
    
    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    public static Language<PawnTokenId> language() {
        return language;
    }
    
    private static final Language<PawnTokenId> language = new LanguageHierarchy<PawnTokenId>() {

        @Override
        protected String mimeType() {
            return "text/x-pawn";
        }
        
        @Override
        protected Collection<PawnTokenId> createTokenIds() {
            return EnumSet.allOf(PawnTokenId.class);
        }

        @Override
        protected Map<String, Collection<PawnTokenId>> createTokenCategories() {
            Map<String, Collection<PawnTokenId>> categories = new HashMap<>();
            categories.put("error", EnumSet.of(
                PawnTokenId.FLOAT_LITERAL_INVALID,
                PawnTokenId.INVALID_COMMENT_END,
                PawnTokenId.ERROR
            ));
            
            EnumSet<PawnTokenId> literals = EnumSet.of(
                PawnTokenId.CELL_LITERAL,
                PawnTokenId.FLOAT_LITERAL,
                PawnTokenId.CHAR_LITERAL,
                PawnTokenId.STRING_LITERAL,
                PawnTokenId.TRUE,
                PawnTokenId.FALSE
            );
            
            categories.put("literal", literals);
            
            EnumSet<PawnTokenId> tags = EnumSet.of(
                PawnTokenId._TAG,
                PawnTokenId.ANYTAG,
                PawnTokenId.BOOLTAG,
                PawnTokenId.FLOATTAG
            );
            
            categories.put("tag", tags);
            
            return categories;
        }

        @Override
        protected Lexer<PawnTokenId> createLexer(LexerRestartInfo<PawnTokenId> info) {
            return new PawnLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<PawnTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            switch (token.id()) {
                case DOC_COMMENT:
                    return LanguageEmbedding.create(PawnDocTokenId.language(), 3,
                            (token.partType() == PartType.COMPLETE) ? 2 : 0);
                case PREPROCESSOR_DIRECTIVE:
                    return LanguageEmbedding.create(PawnPreprocessorTokenId.language(), 1,
                            0);
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
