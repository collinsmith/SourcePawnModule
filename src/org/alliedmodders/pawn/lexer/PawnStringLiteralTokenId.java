package org.alliedmodders.pawn.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public enum PawnStringLiteralTokenId implements TokenId {
    
    TEXT("string"),
    
    BACKSPACE("string-escape"),
    FORM_FEED("string-escape"),
    NEWLINE("string-escape"),
    CR("string-escape"),
    TAB("string-escape"),
    SINGLE_QUOTE("string-escape"),
    DOUBLE_QUOTE("string-escape"),
    BACKSLASH("string-escape"),
    
    PERCENT_ESCAPE("string-escape"),
    PERCENT_ESCAPE_INVALID("string-escape-invalid"),
    
    DECIMAL_ESCAPE("string-escape"),
    DECIMAL_ESCAPE_INVALID("string-escape-invalid"),
    
    HEXADECIMAL_ESCAPE("string-escape"),
    HEXADECIMAL_ESCAPE_INVALID("string-escape-invalid"),
    
    ESCAPE_SEQUENCE_INVALID("string-escape-invalid"),
    
    LINE_CONCATENATION("line-concatenation"),
    ;
    
    private final String primaryCategory;
        
    private PawnStringLiteralTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }
    
    public static Language<PawnStringLiteralTokenId> language() {
        return language;
    }
    
    private static final Language<PawnStringLiteralTokenId> language = new LanguageHierarchy<PawnStringLiteralTokenId>() {

        @Override
        protected String mimeType() {
            return "text/x-pawn-string-literal";
        }
        
        @Override
        protected Collection<PawnStringLiteralTokenId> createTokenIds() {
            return EnumSet.allOf(PawnStringLiteralTokenId.class);
        }

        @Override
        protected Map<String, Collection<PawnStringLiteralTokenId>> createTokenCategories() {
            return super.createTokenCategories();
        }

        @Override
        protected Lexer<PawnStringLiteralTokenId> createLexer(LexerRestartInfo<PawnStringLiteralTokenId> info) {
            return new PawnStringLiteralLexer<PawnStringLiteralTokenId>(info, true);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<PawnStringLiteralTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            return super.embedding(token, languagePath, inputAttributes);
        }
        
    }.language();
}
